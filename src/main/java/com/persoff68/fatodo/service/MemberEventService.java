package com.persoff68.fatodo.service;

import com.persoff68.fatodo.model.Chat;
import com.persoff68.fatodo.model.MemberEvent;
import com.persoff68.fatodo.model.Message;
import com.persoff68.fatodo.model.constant.EventMessageType;
import com.persoff68.fatodo.model.constant.MemberEventType;
import com.persoff68.fatodo.repository.ChatRepository;
import com.persoff68.fatodo.repository.MemberEventRepository;
import com.persoff68.fatodo.repository.MessageRepository;
import com.persoff68.fatodo.service.exception.ModelNotFoundException;
import com.persoff68.fatodo.service.util.ChatUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberEventService {

    private final ChatRepository chatRepository;
    private final MemberEventRepository memberEventRepository;
    private final SystemMessageService systemMessageService;
    private final UserService userService;
    private final PermissionService permissionService;
    private final EntityManager entityManager;
    private final WsService wsService;

    public void addUsersUnsafe(UUID chatId, List<UUID> userIdList) {
        userService.checkUsersExist(userIdList);

        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(ModelNotFoundException::new);

        List<MemberEvent> newMemberList = userIdList.stream()
                .distinct()
                .map(id -> new MemberEvent(chat, id, MemberEventType.ADD_MEMBER))
                .collect(Collectors.toList());

        memberEventRepository.saveAll(newMemberList);
        memberEventRepository.flush();
        entityManager.refresh(chat);

        systemMessageService.createStubMessages(chatId, userIdList);
    }

    public void addUsers(UUID userId, UUID chatId, List<UUID> userIdList) {
        userService.checkUsersExist(userIdList);

        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(ModelNotFoundException::new);

        permissionService.hasEditMembersPermission(chat, userId);

        List<UUID> activeUserIdList = ChatUtils.getActiveUserIdList(chat);
        List<MemberEvent> newMemberList = userIdList.stream()
                .filter(id -> !activeUserIdList.contains(id))
                .distinct()
                .map(id -> new MemberEvent(chat, id, MemberEventType.ADD_MEMBER))
                .collect(Collectors.toList());

        memberEventRepository.saveAll(newMemberList);
        memberEventRepository.flush();
        entityManager.refresh(chat);

        systemMessageService.createStubMessages(chatId, userIdList);
        wsService.sendChatUpdateEvent(chat);
        systemMessageService.createIdsEventMessage(
                userId,
                chatId,
                EventMessageType.ADD_MEMBERS,
                userIdList
        );
    }

    public void removeUsers(UUID userId, UUID chatId, List<UUID> userIdList) {
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(ModelNotFoundException::new);

        permissionService.hasEditMembersPermission(chat, userId);

        List<UUID> activeUserIdList = ChatUtils.getActiveUserIdList(chat);
        List<MemberEvent> memberToDeleteList = userIdList.stream()
                .filter(activeUserIdList::contains)
                .distinct()
                .map(id -> new MemberEvent(chat, id, MemberEventType.DELETE_MEMBER))
                .collect(Collectors.toList());

        if (memberToDeleteList.isEmpty()) {
            throw new ModelNotFoundException();
        }

        memberEventRepository.saveAll(memberToDeleteList);
        memberEventRepository.flush();
        entityManager.refresh(chat);

        wsService.sendChatUpdateEvent(chat);
        systemMessageService.createIdsEventMessage(
                userId,
                chatId,
                EventMessageType.DELETE_MEMBERS,
                userIdList
        );
    }

    public void leaveChat(UUID userId, UUID chatId) {
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(ModelNotFoundException::new);

        permissionService.hasLeaveChatPermission(chat, userId);

        MemberEvent memberEvent = new MemberEvent(chat, userId, MemberEventType.DELETE_MEMBER);
        memberEventRepository.saveAndFlush(memberEvent);
        entityManager.refresh(chat);

        wsService.sendChatUpdateEvent(chat);
    }

    public void clearChat(UUID userId, UUID chatId) {
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(ModelNotFoundException::new);

        permissionService.hasClearChatPermission(chat, userId);

        MemberEvent memberEvent = new MemberEvent(chat, userId, MemberEventType.CLEAR_CHAT);
        memberEventRepository.saveAndFlush(memberEvent);
        entityManager.refresh(chat);

        systemMessageService.createStubMessages(chatId, Collections.singletonList(userId));
    }

    public void deleteChat(UUID userId, UUID chatId) {
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(ModelNotFoundException::new);

        permissionService.hasDeleteChatPermission(chat, userId);

        MemberEvent memberEvent = new MemberEvent(chat, userId, MemberEventType.DELETE_CHAT);
        memberEventRepository.saveAndFlush(memberEvent);
        entityManager.refresh(chat);

        wsService.sendChatUpdateEvent(chat);
        systemMessageService.createSimpleEventMessage(userId, chatId, EventMessageType.LEAVE_CHAT);
    }

}
