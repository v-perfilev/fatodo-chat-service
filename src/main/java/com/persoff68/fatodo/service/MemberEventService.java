package com.persoff68.fatodo.service;

import com.persoff68.fatodo.model.Chat;
import com.persoff68.fatodo.model.MemberEvent;
import com.persoff68.fatodo.model.Message;
import com.persoff68.fatodo.model.constant.EventMessageType;
import com.persoff68.fatodo.model.constant.MemberEventType;
import com.persoff68.fatodo.repository.ChatRepository;
import com.persoff68.fatodo.repository.MemberEventRepository;
import com.persoff68.fatodo.service.client.EventService;
import com.persoff68.fatodo.service.client.WsService;
import com.persoff68.fatodo.service.exception.ModelNotFoundException;
import com.persoff68.fatodo.service.util.ChatUtils;
import com.persoff68.fatodo.service.util.ContactService;
import com.persoff68.fatodo.service.util.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MemberEventService {

    private final ChatRepository chatRepository;
    private final MemberEventRepository memberEventRepository;
    private final SystemMessageService systemMessageService;
    private final UserService userService;
    private final ContactService contactService;
    private final ChatPermissionService chatPermissionService;
    private final EntityManager entityManager;
    private final WsService wsService;
    private final EventService eventService;

    @Transactional
    public void addUsersUnsafe(UUID chatId, List<UUID> userIdList) {
        userService.checkUsersExist(userIdList);

        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(ModelNotFoundException::new);

        List<MemberEvent> newMemberList = userIdList.stream()
                .distinct()
                .map(id -> new MemberEvent(chat, id, MemberEventType.ADD_MEMBER))
                .toList();

        memberEventRepository.saveAll(newMemberList);
        memberEventRepository.flush();
        entityManager.refresh(chat);
    }

    @Transactional
    public void addUsers(UUID userId, UUID chatId, List<UUID> userIdList) {
        contactService.checkIfUsersInContactList(userIdList);

        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(ModelNotFoundException::new);

        chatPermissionService.hasEditMembersPermission(chat, userId);

        // WS
        wsService.sendMemberAddEvent(chat, userIdList, userId);
        // EVENT
        eventService.sendMemberAddEvent(chat, userIdList, userId);

        List<UUID> activeUserIdList = ChatUtils.getActiveUserIdList(chat.getMemberEvents());
        List<MemberEvent> memberEventList = userIdList.stream()
                .filter(id -> !activeUserIdList.contains(id))
                .distinct()
                .map(id -> new MemberEvent(chat, id, MemberEventType.ADD_MEMBER))
                .toList();

        memberEventRepository.saveAll(memberEventList);
        memberEventRepository.flush();
        entityManager.refresh(chat);

        // SYSTEM MESSAGE
        Message systemMessage = systemMessageService
                .createIdsEventMessage(userId, chatId, EventMessageType.ADD_MEMBERS, userIdList);
        wsService.sendMessageNewEvent(systemMessage, userId);
    }

    @Transactional
    public void removeUsers(UUID userId, UUID chatId, List<UUID> userIdList) {
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(ModelNotFoundException::new);

        chatPermissionService.hasEditMembersPermission(chat, userId);

        List<UUID> activeUserIdList = ChatUtils.getActiveUserIdList(chat.getMemberEvents());
        List<MemberEvent> memberEventList = userIdList.stream()
                .filter(activeUserIdList::contains)
                .distinct()
                .map(id -> new MemberEvent(chat, id, MemberEventType.DELETE_MEMBER))
                .toList();

        if (memberEventList.isEmpty()) {
            throw new ModelNotFoundException();
        }

        // WS
        wsService.sendMemberDeleteEvent(chat, userIdList, userId);
        // EVENT
        eventService.sendMemberDeleteEvent(chat, userIdList, userId);

        memberEventRepository.saveAll(memberEventList);
        memberEventRepository.flush();
        entityManager.refresh(chat);

        // SYSTEM MESSAGE
        Message systemMessage = systemMessageService
                .createIdsEventMessage(userId, chatId, EventMessageType.DELETE_MEMBERS, userIdList);
        wsService.sendMessageNewEvent(systemMessage, userId);
    }

    @Transactional
    public void leaveChat(UUID userId, UUID chatId) {
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(ModelNotFoundException::new);

        chatPermissionService.hasLeaveChatPermission(chat, userId);

        // SYSTEM MESSAGE
        Message systemMessage = systemMessageService
                .createSimpleEventMessage(userId, chatId, EventMessageType.LEAVE_CHAT);
        wsService.sendMessageNewEvent(systemMessage, userId);

        // WS
        wsService.sendMemberLeaveEvent(chat, userId);
        // EVENT
        eventService.sendMemberLeaveEvent(chat, userId);

        MemberEvent memberEvent = new MemberEvent(chat, userId, MemberEventType.LEAVE_CHAT);
        memberEventRepository.saveAndFlush(memberEvent);
        entityManager.refresh(chat);
    }

    @Transactional
    public void clearChat(UUID userId, UUID chatId) {
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(ModelNotFoundException::new);

        chatPermissionService.hasClearChatPermission(chat, userId);

        MemberEvent memberEvent = new MemberEvent(chat, userId, MemberEventType.CLEAR_CHAT);
        memberEventRepository.saveAndFlush(memberEvent);
        entityManager.refresh(chat);

        // SYSTEM MESSAGE
        systemMessageService.createPrivateEventMessage(userId, chatId, EventMessageType.CLEAR_CHAT);
    }

    @Transactional
    public void deleteChat(UUID userId, UUID chatId) {
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(ModelNotFoundException::new);

        chatPermissionService.hasDeleteChatPermission(chat, userId);

        // WS
        wsService.sendMemberLeaveEvent(chat, userId);
        // EVENT
        eventService.sendMemberLeaveEvent(chat, userId);

        MemberEvent memberEvent = new MemberEvent(chat, userId, MemberEventType.DELETE_MEMBER);
        memberEventRepository.saveAndFlush(memberEvent);
        entityManager.refresh(chat);

        // SYSTEM MESSAGE
        Message systemMessage = systemMessageService
                .createSimpleEventMessage(userId, chatId, EventMessageType.LEAVE_CHAT);
        wsService.sendMessageNewEvent(systemMessage, userId);
    }

}
