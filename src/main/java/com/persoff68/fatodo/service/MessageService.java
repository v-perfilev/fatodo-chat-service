package com.persoff68.fatodo.service;

import com.persoff68.fatodo.model.Chat;
import com.persoff68.fatodo.model.Message;
import com.persoff68.fatodo.repository.MessageRepository;
import com.persoff68.fatodo.service.exception.ModelNotFoundException;
import com.persoff68.fatodo.service.client.WsService;
import com.persoff68.fatodo.service.util.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class MessageService {

    private final MessageRepository messageRepository;
    private final ChatService chatService;
    private final UserService userService;
    private final ChatPermissionService chatPermissionService;
    private final EntityManager entityManager;
    private final WsService wsService;

    public List<Message> getAllByUserIdAndChatId(UUID userId, UUID chatId, Pageable pageable) {
        Chat chat = chatService.getByUserIdAndId(userId, chatId);
        chatPermissionService.hasReadChatPermission(chat, userId);

        Page<Message> messagePage = messageRepository.findAllByChatIdAndUserId(chatId, userId, pageable);
        return messagePage.toList().stream().toList();
    }

    public Message sendDirect(UUID userId, UUID recipientId, String text, UUID referenceId) {
        userService.checkUserExists(recipientId);
        Chat chat = chatService.getOrCreateDirectByUserIds(userId, recipientId);

        Message reference = getReferenceById(userId, referenceId);
        Message message = Message.of(chat, userId, text, reference);
        message = messageRepository.save(message);
        entityManager.refresh(chat);

        // WS
        wsService.sendMessageNewEvent(message);
        wsService.sendChatLastMessageEvent(message);

        return message;
    }

    public Message send(UUID userId, UUID chatId, String text, UUID referenceId) {
        Chat chat = chatService.getByUserIdAndId(userId, chatId);
        chatPermissionService.hasSendMessagePermission(chat, userId);

        Message reference = getReferenceById(userId, referenceId);
        Message message = Message.of(chat, userId, text, reference);
        message = messageRepository.save(message);
        entityManager.refresh(chat);

        // WS
        wsService.sendMessageNewEvent(message);
        wsService.sendChatLastMessageEvent(message);

        return message;
    }

    public Message edit(UUID userId, UUID messageId, String text) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(ModelNotFoundException::new);
        chatPermissionService.hasEditMessagePermission(message, userId);

        message.setText(text);
        message = messageRepository.save(message);
        Chat chat = message.getChat();
        entityManager.refresh(chat);

        // WS
        wsService.sendMessageUpdateEvent(message);
        if (isMessageLastInChat(message)) {
            wsService.sendChatLastMessageUpdateEvent(message);
        }

        return message;
    }

    public void delete(UUID userId, UUID messageId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(ModelNotFoundException::new);
        chatPermissionService.hasEditMessagePermission(message, userId);

        message.setText(null);
        message.setReference(null);
        message.setDeleted(true);
        message = messageRepository.save(message);
        Chat chat = message.getChat();
        entityManager.refresh(chat);

        // WS
        wsService.sendMessageUpdateEvent(message);
        if (isMessageLastInChat(message)) {
            wsService.sendChatLastMessageUpdateEvent(message);
        }
    }

    public boolean isMessageLastInChat(Message message) {
        UUID messageId = message.getId();
        UUID chatId = message.getChat().getId();
        Message lastMessageInChat = messageRepository.findLastMessageInChat(chatId);
        return lastMessageInChat != null && lastMessageInChat.getId() == messageId;
    }

    private Message getReferenceById(UUID userId, UUID messageId) {
        if (messageId == null) {
            return null;
        }
        Message message = messageRepository.findById(messageId)
                .orElseThrow(ModelNotFoundException::new);
        chatPermissionService.hasReadChatPermission(message.getChat(), userId);
        return message;
    }

}
