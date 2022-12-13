package com.persoff68.fatodo.service;

import com.persoff68.fatodo.model.Chat;
import com.persoff68.fatodo.model.Message;
import com.persoff68.fatodo.model.PageableList;
import com.persoff68.fatodo.repository.MessageRepository;
import com.persoff68.fatodo.service.client.WsService;
import com.persoff68.fatodo.service.exception.ModelNotFoundException;
import com.persoff68.fatodo.service.util.ChatUtils;
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
public class MessageService {

    private final MessageRepository messageRepository;
    private final ChatService chatService;
    private final UserService userService;
    private final ChatPermissionService chatPermissionService;
    private final EntityManager entityManager;
    private final WsService wsService;

    @Transactional(readOnly = true)
    public PageableList<Message> getAllByUserIdAndChatId(UUID userId, UUID chatId, Pageable pageable) {
        Chat chat = chatService.getByUserIdAndId(userId, chatId);
        chatPermissionService.hasReadChatPermission(chat, userId);

        Page<Message> messagePage = messageRepository.findAllByChatIdAndUserId(chatId, userId, pageable);
        return PageableList.of(messagePage.getContent(), messagePage.getTotalElements());
    }

    @Transactional
    public List<Message> getAllAllowedByIds(UUID userId, List<UUID> messageIdList) {
        List<Message> messageList = messageRepository.findAllByIds(messageIdList);
        return messageList.stream()
                .filter(m -> ChatUtils.wasUserInChat(m.getChat(), userId))
                .toList();
    }

    @Transactional
    public Message sendDirect(UUID userId, UUID recipientId, String text) {
        userService.checkUserExists(recipientId);
        Chat chat = chatService.getOrCreateDirectByUserIds(userId, recipientId);

        Message message = Message.of(chat, userId, text);
        message = messageRepository.save(message);
        entityManager.refresh(chat);

        // WS
        wsService.sendMessageNewEvent(message, userId);

        return message;
    }

    @Transactional
    public Message send(UUID userId, UUID chatId, String text) {
        Chat chat = chatService.getByUserIdAndId(userId, chatId);
        chatPermissionService.hasSendMessagePermission(chat, userId);

        Message message = Message.of(chat, userId, text);
        message = messageRepository.save(message);
        entityManager.refresh(chat);

        // WS
        wsService.sendMessageNewEvent(message, userId);

        return message;
    }

    @Transactional
    public Message edit(UUID userId, UUID messageId, String text) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(ModelNotFoundException::new);
        chatPermissionService.hasEditMessagePermission(message, userId);

        message.setText(text);
        message = messageRepository.save(message);
        Chat chat = message.getChat();
        entityManager.refresh(chat);

        // WS
        wsService.sendMessageUpdateEvent(message, userId);

        return message;
    }

    @Transactional
    public void delete(UUID userId, UUID messageId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(ModelNotFoundException::new);
        chatPermissionService.hasEditMessagePermission(message, userId);

        message.setText(null);
        message.setDeleted(true);
        message = messageRepository.save(message);
        Chat chat = message.getChat();
        entityManager.refresh(chat);

        // WS
        wsService.sendMessageUpdateEvent(message, userId);
    }

}
