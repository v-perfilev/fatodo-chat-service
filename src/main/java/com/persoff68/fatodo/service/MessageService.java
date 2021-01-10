package com.persoff68.fatodo.service;

import com.persoff68.fatodo.model.Chat;
import com.persoff68.fatodo.model.Message;
import com.persoff68.fatodo.repository.MessageRepository;
import com.persoff68.fatodo.service.exception.ModelNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;
    private final ChatService chatService;
    private final UserService userService;
    private final PermissionService permissionService;

    public void sendDirect(UUID userId, UUID recipientId, String text, UUID forwardedMessageId) {
        userService.checkUserExists(recipientId);
        Chat chat = chatService.getDirectByUserIds(userId, recipientId);
        Message message = Message.of(chat.getId(), userId, text, forwardedMessageId);
        messageRepository.save(message);
    }

    public void send(UUID userId, UUID chatId, String text, UUID forwardedMessageId) {
        Chat chat = chatService.getById(chatId);
        permissionService.hasSendMessagePermission(chat, userId);
        Message message = Message.of(chat.getId(), userId, text, forwardedMessageId);
        messageRepository.save(message);
    }

    public void edit(UUID userId, UUID messageId, String text, UUID forwardedMessageId) {
        Message message = messageRepository.findByIdAndUserId(messageId, userId)
                .orElseThrow(ModelNotFoundException::new);
        permissionService.hasEditMessagePermission(message, userId);
        message.setText(text);
        message.setForwardedMessageId(forwardedMessageId);
        messageRepository.save(message);
    }

    public void delete(UUID userId, UUID messageId) {
        Message message = messageRepository.findByIdAndUserId(messageId, userId)
                .orElseThrow(ModelNotFoundException::new);
        permissionService.hasEditMessagePermission(message, userId);
        messageRepository.delete(message);
    }

}
