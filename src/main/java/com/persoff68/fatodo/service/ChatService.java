package com.persoff68.fatodo.service;

import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.persoff68.fatodo.model.AbstractModel;
import com.persoff68.fatodo.model.Chat;
import com.persoff68.fatodo.model.ChatContainer;
import com.persoff68.fatodo.model.Message;
import com.persoff68.fatodo.model.PageableList;
import com.persoff68.fatodo.model.constant.EventMessageType;
import com.persoff68.fatodo.repository.ChatRepository;
import com.persoff68.fatodo.repository.MessageRepository;
import com.persoff68.fatodo.service.client.EventService;
import com.persoff68.fatodo.service.client.WsService;
import com.persoff68.fatodo.service.exception.ModelAlreadyExistsException;
import com.persoff68.fatodo.service.exception.ModelNotFoundException;
import com.persoff68.fatodo.service.util.ChatUtils;
import com.persoff68.fatodo.service.util.ContactService;
import com.persoff68.fatodo.service.util.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRepository chatRepository;
    private final MessageRepository messageRepository;
    private final ContactService contactService;
    private final MemberEventService memberEventService;
    private final ChatPermissionService chatPermissionService;
    private final SystemMessageService systemMessageService;
    private final UserService userService;
    private final WsService wsService;
    private final EventService eventService;


    @Transactional(readOnly = true)
    public PageableList<ChatContainer> getAllByUserId(UUID userId, Pageable pageable) {
        Page<Message> messagePage = messageRepository.findAllByUserId(userId, pageable);
        List<ChatContainer> chatList = messagePage.getContent().stream().map(ChatContainer::new).toList();
        return PageableList.of(chatList, messagePage.getTotalElements());
    }

    @Transactional(readOnly = true)
    public List<ChatContainer> getFilteredByUserId(UUID userId, String filter) {
        List<UUID> userIdList = userService.getUserIdsByUsernamePart(filter);
        List<Chat> chatList = chatRepository.findAllByUserId(userId);
        List<UUID> chatIdList = chatList.stream()
                .filter(chat -> {
                    boolean filteredByTitle = chat.getTitle() != null && chat.getTitle().contains(filter);
                    boolean filteredByUsers = ChatUtils.isAnyUserInChat(chat, userIdList);
                    return filteredByTitle || filteredByUsers;
                })
                .map(Chat::getId)
                .toList();
        List<Message> messageList = messageRepository.findAllByChatIdListAndUserId(chatIdList, userId);
        return messageList.stream().map(ChatContainer::new).toList();
    }

    @Transactional(readOnly = true)
    public List<Chat> getAllAllowedByIds(UUID userId, List<UUID> chatIdList) {
        return chatRepository.findAllByUserIdAndIds(userId, chatIdList);
    }

    @Transactional(readOnly = true)
    public Chat getByUserIdAndId(UUID userId, UUID chatId) {
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(ModelNotFoundException::new);
        chatPermissionService.hasReadChatPermission(chat, userId);
        return chat;
    }

    @Transactional
    public Chat createDirect(UUID firstUserId, UUID secondUserId) {
        Chat oldChat = getDirectByUserIds(firstUserId, secondUserId);
        if (oldChat != null) {
            throw new ModelAlreadyExistsException();
        }
        List<UUID> userIdList = List.of(firstUserId, secondUserId);
        Chat chat = create(userIdList, true);

        systemMessageService.createIdsEventMessage(
                firstUserId,
                chat.getId(),
                EventMessageType.CREATE_DIRECT_CHAT,
                Collections.singletonList(secondUserId)
        );

        // WS
        wsService.sendChatNewEvent(chat);
        // EVENT
        eventService.sendChatCreateEvent(chat.getId(), firstUserId, List.of(secondUserId));

        return chat;
    }

    @Transactional
    public Chat createIndirect(UUID userId, List<UUID> userIdList) {
        List<UUID> allUserIdList = new ArrayList<>(userIdList);
        allUserIdList.add(userId);
        Chat chat = create(allUserIdList, false);

        systemMessageService.createIdsEventMessage(
                userId,
                chat.getId(),
                EventMessageType.CREATE_CHAT,
                userIdList
        );

        // WS
        wsService.sendChatNewEvent(chat);
        // EVENT
        eventService.sendChatCreateEvent(chat.getId(), userId, userIdList);

        return chat;
    }

    @Transactional
    public Chat rename(UUID userId, UUID chatId, String title) {
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(ModelNotFoundException::new);
        chatPermissionService.hasRenameChatPermission(chat, userId);

        chat.setTitle(title);
        chat = chatRepository.save(chat);

        systemMessageService.createTextEventMessage(userId, chatId, EventMessageType.RENAME_CHAT, title);

        // WS
        wsService.sendChatUpdateEvent(chat);
        // EVENT
        List<UUID> recipientIdList = ChatUtils.getActiveUserIdList(chat);
        eventService.sendChatUpdateEvent(recipientIdList, chat.getId(), userId);

        return chat;
    }

    @Transactional
    Chat getOrCreateDirectByUserIds(UUID firstUserId, UUID secondUserId) {
        List<UUID> userIdList = List.of(firstUserId, secondUserId);
        Supplier<Chat> createChatSupplier = () -> createDirect(firstUserId, secondUserId);
        return chatRepository.findDirectChat(userIdList)
                .orElseGet(createChatSupplier);
    }

    @Transactional
    protected Chat create(List<UUID> userIdList, boolean isDirect) {
        if (isDirect) {
            userService.checkUsersExist(userIdList);
        } else {
            contactService.checkIfUsersInContactList(userIdList);
        }
        Chat chat = chatRepository.saveAndFlush(new Chat(isDirect));
        memberEventService.addUsersUnsafe(chat.getId(), userIdList);
        return chat;
    }

    @Transactional(readOnly = true)
    protected Chat getDirectByUserIds(UUID firstUserId, UUID secondUserId) {
        List<UUID> userIdList = List.of(firstUserId, secondUserId);
        return chatRepository.findDirectChat(userIdList)
                .orElse(null);
    }

    @Transactional(readOnly = true)
    public Multimap<UUID, UUID> getUnreadMessagesMap(UUID userId) {
        List<Message> unreadMessageList = messageRepository.findAllUnreadMessagesByUserId(userId);
        Multimap<UUID, Message> unreadMessageMultimap = Multimaps.index(unreadMessageList, m -> m.getChat().getId());
        return Multimaps.transformValues(unreadMessageMultimap, AbstractModel::getId);
    }

}
