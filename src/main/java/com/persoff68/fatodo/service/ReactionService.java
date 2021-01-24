package com.persoff68.fatodo.service;

import com.persoff68.fatodo.model.Message;
import com.persoff68.fatodo.model.Reaction;
import com.persoff68.fatodo.repository.MessageRepository;
import com.persoff68.fatodo.repository.ReactionRepository;
import com.persoff68.fatodo.service.exception.ModelNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReactionService {

    private final ReactionRepository reactionRepository;
    private final MessageRepository messageRepository;
    private final PermissionService permissionService;
    private final EntityManager entityManager;

    public void setLike(UUID userId, UUID messageId) {
        set(userId, messageId, Reaction.Type.LIKE);
    }

    public void setDislike(UUID userId, UUID messageId) {
        set(userId, messageId, Reaction.Type.DISLIKE);
    }

    @Transactional
    public void remove(UUID userId, UUID messageId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(ModelNotFoundException::new);
        permissionService.hasReadMessagePermission(message.getChat(), userId);
        Reaction.ReactionId id = new Reaction.ReactionId(messageId, userId);
        reactionRepository.deleteById(id);
        entityManager.refresh(message);
    }

    @Transactional
    protected void set(UUID userId, UUID messageId, Reaction.Type type) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(ModelNotFoundException::new);
        permissionService.hasReadMessagePermission(message.getChat(), userId);
        Reaction.ReactionId id = new Reaction.ReactionId(messageId, userId);
        Reaction reaction = reactionRepository.findById(id)
                .orElse(new Reaction(messageId, userId));
        reaction.setType(type);
        reactionRepository.save(reaction);
        entityManager.refresh(message);
    }

}
