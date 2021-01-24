package com.persoff68.fatodo.service;

import com.persoff68.fatodo.model.Chat;
import com.persoff68.fatodo.model.Message;
import com.persoff68.fatodo.repository.MessageRepository;
import com.persoff68.fatodo.service.exception.ModelNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;
    private final ChatService chatService;
    private final UserService userService;
    private final PermissionService permissionService;
    private final EntityManager entityManager;

    @Transactional
    public void sendDirect(UUID userId, UUID recipientId, String text, UUID forwardedMessageId) {
        userService.checkUserExists(recipientId);
        Chat chat = chatService.getDirectByUserIds(userId, recipientId);

        Message message = new Message(chat, userId, text, getForwardedById(userId, forwardedMessageId));
        messageRepository.save(message);
        entityManager.refresh(chat);
    }

    @Transactional
    public void send(UUID userId, UUID chatId, String text, UUID forwardedMessageId) {
        Chat chat = chatService.getById(chatId);
        permissionService.hasSendMessagePermission(chat, userId);

        Message message = new Message(chat, userId, text, getForwardedById(userId, forwardedMessageId));
        messageRepository.save(message);
        entityManager.refresh(chat);
    }

    @Transactional
    public void edit(UUID userId, UUID messageId, String text, UUID forwardedMessageId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(ModelNotFoundException::new);
        Chat chat = message.getChat();
        permissionService.hasEditMessagePermission(message, userId);

        message.setText(text);
        message.setForwardedMessage(getForwardedById(userId, forwardedMessageId));
        messageRepository.save(message);
        entityManager.refresh(chat);
    }

    @Transactional
    public void delete(UUID userId, UUID messageId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(ModelNotFoundException::new);
        Chat chat = message.getChat();
        permissionService.hasEditMessagePermission(message, userId);

        messageRepository.delete(message);
        entityManager.refresh(chat);
    }

    private Message getForwardedById(UUID userId, UUID messageId) {
        if (messageId == null) {
            return null;
        }
        Message message = messageRepository.findById(messageId)
                .orElseThrow(ModelNotFoundException::new);
        permissionService.hasReadMessagePermission(message.getChat(), userId);
        return message;
    }

}
