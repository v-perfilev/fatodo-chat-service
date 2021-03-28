package com.persoff68.fatodo.service;

import com.persoff68.fatodo.model.Chat;
import com.persoff68.fatodo.model.Message;
import com.persoff68.fatodo.model.constant.EventMessageType;
import com.persoff68.fatodo.repository.ChatRepository;
import com.persoff68.fatodo.repository.MessageRepository;
import com.persoff68.fatodo.repository.projection.ChatMessagesStats;
import com.persoff68.fatodo.service.exception.ModelAlreadyExistsException;
import com.persoff68.fatodo.service.exception.ModelNotFoundException;
import com.persoff68.fatodo.service.util.ChatUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

@Service
@RequiredArgsConstructor
@Transactional
public class ChatService {

    private final ChatRepository chatRepository;
    private final MessageRepository messageRepository;
    private final UserService userService;
    private final MemberEventService memberEventService;
    private final PermissionService permissionService;
    private final WsService wsService;
    private final SystemMessageService systemMessageService;

    public Map<Chat, Message> getAllByUserId(UUID userId, Pageable pageable) {
        Page<Message> messagePage = messageRepository.findAllByUserId(userId, pageable);
        return messagePage.toList().stream()
                .collect(ChatUtils.CHAT_MAP_COLLECTOR);
    }

    public Chat getByUserIdAndId(UUID userId, UUID chatId) {
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(ModelNotFoundException::new);
        permissionService.hasReadChatPermission(chat, userId);
        return chat;
    }

    public Chat createDirect(UUID firstUserId, UUID secondUserId) {
        Chat chat = getDirectByUserIds(firstUserId, secondUserId);
        if (chat != null) {
            throw new ModelAlreadyExistsException();
        }
        List<UUID> userIdList = List.of(firstUserId, secondUserId);
        Chat createdChat = create(userIdList, true);

        systemMessageService.createIdsEventMessage(
                firstUserId,
                createdChat.getId(),
                EventMessageType.CREATE_DIRECT_CHAT,
                Collections.singletonList(secondUserId)
        );

        return createdChat;
    }

    public Chat createIndirect(UUID userId, List<UUID> userIdList) {
        List<UUID> allUserIdList = new ArrayList<>(userIdList);
        allUserIdList.add(userId);
        Chat createdChat = create(allUserIdList, false);

        systemMessageService.createIdsEventMessage(
                userId,
                createdChat.getId(),
                EventMessageType.CREATE_CHAT,
                userIdList
        );

        return createdChat;
    }

    public Chat rename(UUID userId, UUID chatId, String title) {
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(ModelNotFoundException::new);
        permissionService.hasRenameChatPermission(chat, userId);

        chat.setTitle(title);
        chat = chatRepository.save(chat);

        wsService.sendChatUpdateEvent(chat);
        systemMessageService.createTextEventMessage(userId, chatId, EventMessageType.RENAME_CHAT, title);

        return chat;
    }

    Chat getOrCreateDirectByUserIds(UUID firstUserId, UUID secondUserId) {
        List<UUID> userIdList = List.of(firstUserId, secondUserId);
        Supplier<Chat> createChatSupplier = () -> createDirect(firstUserId, secondUserId);
        return chatRepository.findDirectChat(userIdList)
                .orElseGet(createChatSupplier);
    }

    protected Chat create(List<UUID> userIdList, boolean isDirect) {
        userService.checkUsersExist(userIdList);
        Chat chat = chatRepository.saveAndFlush(new Chat(isDirect));
        memberEventService.addUsersUnsafe(chat.getId(), userIdList);

        wsService.sendChatNewEvent(chat);
        return chat;
    }

    protected Chat getDirectByUserIds(UUID firstUserId, UUID secondUserId) {
        List<UUID> userIdList = List.of(firstUserId, secondUserId);
        return chatRepository.findDirectChat(userIdList)
                .orElse(null);
    }

    public Map<UUID, Integer> getUnreadMessagesMap(UUID userId) {
        List<ChatMessagesStats> unreadMessageCountList = messageRepository.findAllUnreadMessages(userId);
        Map<UUID, Integer> unreadMessagesMap = new HashMap<>();
        unreadMessageCountList.forEach(stats -> {
            UUID chatId = UUID.nameUUIDFromBytes(stats.getChatId());
            unreadMessagesMap.put(chatId, stats.getMessagesCount());
        });
        return unreadMessagesMap;
    }
}
