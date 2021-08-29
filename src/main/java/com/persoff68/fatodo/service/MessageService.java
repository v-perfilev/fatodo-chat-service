package com.persoff68.fatodo.service;

import com.persoff68.fatodo.model.Chat;
import com.persoff68.fatodo.model.Message;
import com.persoff68.fatodo.repository.MessageRepository;
import com.persoff68.fatodo.service.exception.ModelNotFoundException;
import com.persoff68.fatodo.service.ws.WsService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class MessageService {

    private final MessageRepository messageRepository;
    private final ChatService chatService;
    private final UserService userService;
    private final PermissionService permissionService;
    private final EntityManager entityManager;
    private final WsService wsService;

    public List<Message> getAllByUserIdAndChatId(UUID userId, UUID chatId, Pageable pageable) {
        Chat chat = chatService.getByUserIdAndId(userId, chatId);
        permissionService.hasReadChatPermission(chat, userId);

        Page<Message> messagePage = messageRepository.findAllByChatIdAndUserId(chatId, userId, pageable);
        return messagePage.toList().stream()
                .filter(m -> !m.isStub())
                .collect(Collectors.toList());
    }

    public Message sendDirect(UUID userId, UUID recipientId, String text, UUID forwardedMessageId) {
        userService.checkUserExists(recipientId);
        Chat chat = chatService.getOrCreateDirectByUserIds(userId, recipientId);

        Message message = Message.of(chat, userId, text, getReferenceById(userId, forwardedMessageId));
        message = messageRepository.save(message);
        entityManager.refresh(chat);

        // WS
        wsService.sendMessageNewEvent(message);
        wsService.sendChatLastMessageEvent(message);

        return message;
    }

    public Message send(UUID userId, UUID chatId, String text, UUID referenceId) {
        Chat chat = chatService.getByUserIdAndId(userId, chatId);
        permissionService.hasSendMessagePermission(chat, userId);

        Message message = Message.of(chat, userId, text, getReferenceById(userId, referenceId));
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
        permissionService.hasEditMessagePermission(message, userId);

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
        permissionService.hasEditMessagePermission(message, userId);

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
        permissionService.hasReadChatPermission(message.getChat(), userId);
        return message;
    }

}
