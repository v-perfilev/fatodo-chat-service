package com.persoff68.fatodo.service;

import com.persoff68.fatodo.model.Chat;
import com.persoff68.fatodo.model.ChatMessage;
import com.persoff68.fatodo.repository.ChatMessageRepository;
import com.persoff68.fatodo.service.exception.ModelNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;
    private final ChatService chatService;
    private final UserService userService;

    public void sendToChat(UUID userId, UUID chatId, String text) {
        chatService.checkPermission(chatId, userId);
        ChatMessage chatMessage = new ChatMessage(chatId, userId, text);
        chatMessageRepository.save(chatMessage);
    }

    public void sendToUser(UUID userId, UUID recipientId, String text) {
        userService.checkUsersExist(Collections.singletonList(recipientId));

        Chat chat = chatService.getDirectChatForUsers(userId, recipientId);

        ChatMessage chatMessage = new ChatMessage(chat.getId(), userId, text);
        chatMessageRepository.save(chatMessage);
    }

    public void edit(UUID userId, UUID messageId, String text) {
        ChatMessage chatMessage = chatMessageRepository.findByIdAndUserId(messageId, userId)
                .orElseThrow(ModelNotFoundException::new);
        chatMessage.setText(text);
        chatMessageRepository.save(chatMessage);
    }

    public void delete(UUID userId, UUID messageId) {
        ChatMessage chatMessage = chatMessageRepository.findByIdAndUserId(messageId, userId)
                .orElseThrow(ModelNotFoundException::new);
        chatMessage.setDeleted(true);
        chatMessageRepository.save(chatMessage);
    }

}
