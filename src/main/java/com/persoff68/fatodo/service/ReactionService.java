package com.persoff68.fatodo.service;

import com.persoff68.fatodo.model.Message;
import com.persoff68.fatodo.model.Reaction;
import com.persoff68.fatodo.model.constant.ReactionType;
import com.persoff68.fatodo.repository.MessageRepository;
import com.persoff68.fatodo.service.client.EventService;
import com.persoff68.fatodo.service.client.WsService;
import com.persoff68.fatodo.service.exception.ModelNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class ReactionService {

    private final MessageRepository messageRepository;
    private final ChatPermissionService chatPermissionService;
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

        message.getReactions().stream()
                .filter(reaction -> reaction.getUserId().equals(userId))
                .findFirst().ifPresent(reaction -> {
                    message.getReactions().remove(reaction);
                    messageRepository.save(message);

                    // WS
                    reaction.setType(ReactionType.NONE);
                    wsService.sendMessageReactionEvent(reaction);
                    wsService.sendMessageReactionIncomingEvent(reaction);
                    // EVENT
                    eventService.sendChatReactionEvent(message.getUserId(), message.getChat().getId(), userId, null);
                });
    }

    protected void set(UUID userId, UUID messageId, ReactionType type) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(ModelNotFoundException::new);
        chatPermissionService.hasReactOnMessagePermission(message, userId);

        Optional<Reaction> reactionOptional = message.getReactions().stream()
                .filter(reaction -> reaction.getUserId().equals(userId)).findFirst();

        Reaction reaction;
        if (reactionOptional.isPresent()) {
            reaction = reactionOptional.get();
            reaction.setType(type);
            reaction.setDate(new Date());
        } else {
            reaction = Reaction.of(message, userId, type);
            message.getReactions().add(reaction);
        }
        messageRepository.save(message);

        // WS
        wsService.sendMessageReactionEvent(reaction);
        wsService.sendMessageReactionIncomingEvent(reaction);
        // EVENT
        eventService.sendChatReactionEvent(message.getUserId(), message.getChat().getId(), userId, type);
    }

}
