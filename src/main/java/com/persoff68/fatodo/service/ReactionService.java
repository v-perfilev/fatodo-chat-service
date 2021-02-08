package com.persoff68.fatodo.service;

import com.persoff68.fatodo.model.Message;
import com.persoff68.fatodo.model.Reaction;
import com.persoff68.fatodo.model.ReactionId;
import com.persoff68.fatodo.model.ReactionType;
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
@Transactional
public class ReactionService {

    private final ReactionRepository reactionRepository;
    private final MessageRepository messageRepository;
    private final PermissionService permissionService;
    private final EntityManager entityManager;

    public void setLike(UUID userId, UUID messageId) {
        set(userId, messageId, ReactionType.LIKE);
    }

    public void setDislike(UUID userId, UUID messageId) {
        set(userId, messageId, ReactionType.DISLIKE);
    }

    public void remove(UUID userId, UUID messageId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(ModelNotFoundException::new);
        permissionService.hasReactOnMessagePermission(message, userId);

        ReactionId id = new ReactionId(messageId, userId);
        reactionRepository.deleteById(id);
        entityManager.refresh(message);
    }

    protected void set(UUID userId, UUID messageId, ReactionType type) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(ModelNotFoundException::new);
        permissionService.hasReactOnMessagePermission(message, userId);

        ReactionId id = new ReactionId(messageId, userId);
        Reaction reaction = reactionRepository.findById(id)
                .orElse(new Reaction(messageId, userId));
        reaction.setType(type);
        reactionRepository.save(reaction);
        entityManager.refresh(message);
    }

}
