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

    public void sendToChat(UUID userId, UUID chatId, String text) {
        chatService.checkPermission(chatId, userId);
        Message message = new Message(chatId, userId, text);
        messageRepository.save(message);
    }

    public void sendToUser(UUID userId, UUID recipientId, String text) {
        userService.checkUserExists(recipientId);
        Chat chat = chatService.getDirectChatForUsers(userId, recipientId);
        Message message = new Message(chat.getId(), userId, text);
        messageRepository.save(message);
    }

    public void edit(UUID userId, UUID messageId, String text) {
        Message message = messageRepository.findByIdAndUserId(messageId, userId)
                .orElseThrow(ModelNotFoundException::new);
        message.setText(text);
        messageRepository.save(message);
    }

    public void delete(UUID userId, UUID messageId) {
        Message message = messageRepository.findByIdAndUserId(messageId, userId)
                .orElseThrow(ModelNotFoundException::new);
        message.setDeleted(true);
        messageRepository.save(message);
    }

}
