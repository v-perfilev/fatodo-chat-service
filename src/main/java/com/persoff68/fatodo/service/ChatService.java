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

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.stream.Stream;

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
        List<UUID> chatIdList = chatList.stream().filter(chat -> {
            boolean filteredByTitle = chat.getTitle() != null && chat.getTitle().contains(filter);
            boolean filteredByUsers = ChatUtils.isAnyUserInChat(chat, userIdList);
            return filteredByTitle || filteredByUsers;
        }).map(Chat::getId).toList();
        List<Message> messageList = messageRepository.findAllByChatIdListAndUserId(chatIdList, userId);
        return messageList.stream().map(ChatContainer::new).toList();
    }

    @Transactional(readOnly = true)
    public List<Chat> getAllAllowedByIds(UUID userId, List<UUID> chatIdList) {
        return chatRepository.findAllByUserIdAndIds(userId, chatIdList);
    }

    @Transactional(readOnly = true)
    public Chat getByUserIdAndId(UUID userId, UUID chatId) {
        Chat chat = chatRepository.findById(chatId).orElseThrow(ModelNotFoundException::new);
        chatPermissionService.hasReadChatPermission(chat, userId);
        return chat;
    }

    @Transactional
    public ChatContainer createDirect(UUID firstUserId, UUID secondUserId) {
        Chat oldChat = getDirectByUserIds(firstUserId, secondUserId);
        if (oldChat != null) {
            throw new ModelAlreadyExistsException();
        }
        List<UUID> userIdList = List.of(firstUserId, secondUserId);

        userService.checkUsersExist(userIdList);
        Chat chat = chatRepository.saveAndFlush(new Chat(true));
        memberEventService.addUsersUnsafe(chat.getId(), userIdList);

        // SYSTEM MESSAGE
        Message systemMessage = systemMessageService.createIdsEventMessage(firstUserId, chat.getId(),
                EventMessageType.CREATE_DIRECT_CHAT, Collections.singletonList(secondUserId));

        // WS
        wsService.sendChatNewEvent(chat, systemMessage, firstUserId);
        // EVENT
        eventService.sendChatNewEvent(chat, firstUserId);

        return new ChatContainer(systemMessage);
    }

    @Transactional
    public ChatContainer createIndirect(UUID userId, List<UUID> userIdList) {
        List<UUID> allUserIdList = Stream.concat(userIdList.stream(), Stream.of(userId)).toList();

        contactService.checkIfUsersInContactList(userIdList);
        Chat chat = chatRepository.saveAndFlush(new Chat(false));
        memberEventService.addUsersUnsafe(chat.getId(), allUserIdList);

        // SYSTEM MESSAGE
        Message systemMessage = systemMessageService.createIdsEventMessage(userId, chat.getId(),
                EventMessageType.CREATE_CHAT, userIdList);

        // WS
        wsService.sendChatNewEvent(chat, systemMessage, userId);
        // EVENT
        eventService.sendChatNewEvent(chat, userId);

        return new ChatContainer(systemMessage);
    }

    @Transactional
    public ChatContainer rename(UUID userId, UUID chatId, String title) {
        Chat chat = chatRepository.findById(chatId).orElseThrow(ModelNotFoundException::new);
        chatPermissionService.hasRenameChatPermission(chat, userId);

        chat.setTitle(title);
        chat = chatRepository.save(chat);

        // SYSTEM MESSAGE
        Message systemMessage = systemMessageService.createTextEventMessage(userId, chatId,
                EventMessageType.RENAME_CHAT, title);
        wsService.sendMessageNewEvent(systemMessage, userId);

        // WS
        wsService.sendChatUpdateEvent(chat, systemMessage, userId);
        // EVENT
        eventService.sendChatUpdateEvent(chat, userId);

        return new ChatContainer(systemMessage);
    }

    @Transactional
    public Chat getOrCreateDirectByUserIds(UUID firstUserId, UUID secondUserId) {
        List<UUID> userIdList = List.of(firstUserId, secondUserId);
        Supplier<Chat> createChatSupplier = () -> createDirect(firstUserId, secondUserId).getChat();
        return chatRepository.findDirectChat(userIdList).orElseGet(createChatSupplier);
    }

    @Transactional(readOnly = true)
    public Multimap<UUID, UUID> getUnreadMessagesMap(UUID userId) {
        List<Message> unreadMessageList = messageRepository.findAllUnreadMessagesByUserId(userId);
        Multimap<UUID, Message> unreadMessageMultimap = Multimaps.index(unreadMessageList, m -> m.getChat().getId());
        return Multimaps.transformValues(unreadMessageMultimap, AbstractModel::getId);
    }

    private Chat getDirectByUserIds(UUID firstUserId, UUID secondUserId) {
        List<UUID> userIdList = List.of(firstUserId, secondUserId);
        return chatRepository.findDirectChat(userIdList).orElse(null);
    }

}
