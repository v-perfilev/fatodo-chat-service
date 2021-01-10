package com.persoff68.fatodo.service;

import com.persoff68.fatodo.model.Chat;
import com.persoff68.fatodo.model.Message;
import com.persoff68.fatodo.model.Reaction;
import com.persoff68.fatodo.repository.MessageRepository;
import com.persoff68.fatodo.repository.ReactionRepository;
import com.persoff68.fatodo.service.exception.ModelNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReactionService {

    private final ReactionRepository reactionRepository;
    private final MessageRepository messageRepository;
    private final PermissionService permissionService;

    public void addReaction(UUID userId, UUID messageId, Reaction.Type type) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(ModelNotFoundException::new);
        Chat chat = Optional.of(message.getChat())
                .orElseThrow(ModelNotFoundException::new);
        permissionService.hasReadMessagePermission(chat, userId);
        Reaction.ReactionId id = new Reaction.ReactionId(messageId, userId);
        Reaction reaction = reactionRepository.findById(id)
                .orElse(Reaction.of(messageId, userId));
        reaction.setType(type);
        reactionRepository.save(reaction);
    }

    public void removeReaction(UUID userId, UUID messageId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(ModelNotFoundException::new);
        Chat chat = Optional.of(message.getChat())
                .orElseThrow(ModelNotFoundException::new);
        permissionService.hasReadMessagePermission(chat, userId);
        Reaction.ReactionId id = new Reaction.ReactionId(messageId, userId);
        reactionRepository.deleteById(id);
    }

}
