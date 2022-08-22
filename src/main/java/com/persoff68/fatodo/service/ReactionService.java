package com.persoff68.fatodo.service;

import com.persoff68.fatodo.model.Message;
import com.persoff68.fatodo.model.Reaction;
import com.persoff68.fatodo.model.constant.ReactionType;
import com.persoff68.fatodo.repository.MessageRepository;
import com.persoff68.fatodo.repository.ReactionRepository;
import com.persoff68.fatodo.service.client.EventService;
import com.persoff68.fatodo.service.client.WsService;
import com.persoff68.fatodo.service.exception.ModelNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class ReactionService {

    private final ReactionRepository reactionRepository;
    private final MessageRepository messageRepository;
    private final ChatPermissionService chatPermissionService;
    private final EntityManager entityManager;
    private final WsService wsService;
    private final EventService eventService;

    public void setLike(UUID userId, UUID messageId) {
        set(userId, messageId, ReactionType.LIKE);
    }

    public void setDislike(UUID userId, UUID messageId) {
        set(userId, messageId, ReactionType.DISLIKE);
    }

    public void remove(UUID userId, UUID messageId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(ModelNotFoundException::new);
        chatPermissionService.hasReactOnMessagePermission(message, userId);

        Reaction.ReactionId id = new Reaction.ReactionId(messageId, userId);
        Optional<Reaction> reactionOptional = reactionRepository.findById(id);

        reactionOptional.ifPresent(reaction -> {
            reactionRepository.delete(reaction);
            reactionRepository.flush();
            entityManager.refresh(message);

            // WS
            reaction.setType(ReactionType.NONE);
            wsService.sendMessageReactionEvent(reaction, message.getChat());
            wsService.sendMessageReactionIncomingEvent(reaction, message.getChat(), message.getUserId());
            // EVENT
            eventService.sendChatReactionEvent(message.getUserId(), message.getChat().getId(), userId, null);
        });
    }

    protected void set(UUID userId, UUID messageId, ReactionType type) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(ModelNotFoundException::new);
        chatPermissionService.hasReactOnMessagePermission(message, userId);

        Reaction.ReactionId id = new Reaction.ReactionId(messageId, userId);
        Reaction reaction = reactionRepository.findById(id)
                .orElse(new Reaction(messageId, userId, type));
        reaction.setType(type);
        reaction.setTimestamp(new Date());
        reaction = reactionRepository.saveAndFlush(reaction);
        entityManager.refresh(message);

        // WS
        wsService.sendMessageReactionEvent(reaction, message.getChat());
        wsService.sendMessageReactionIncomingEvent(reaction, message.getChat(), message.getUserId());
        // EVENT
        eventService.sendChatReactionEvent(message.getUserId(), message.getChat().getId(), userId, type);
    }

}
