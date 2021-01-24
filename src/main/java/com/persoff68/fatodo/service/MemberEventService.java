package com.persoff68.fatodo.service;

import com.persoff68.fatodo.model.Chat;
import com.persoff68.fatodo.model.MemberEvent;
import com.persoff68.fatodo.repository.ChatRepository;
import com.persoff68.fatodo.repository.MemberEventRepository;
import com.persoff68.fatodo.service.exception.ModelNotFoundException;
import com.persoff68.fatodo.service.util.ChatUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MemberEventService {

    private final ChatRepository chatRepository;
    private final MemberEventRepository memberEventRepository;
    private final UserService userService;
    private final PermissionService permissionService;

    public void addUsersToChat(UUID chatId, UUID currentUserId, List<UUID> userIdList) {
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(ModelNotFoundException::new);

        permissionService.hasEditMembersPermission(chat, currentUserId);
        userService.checkUsersExist(userIdList);

        List<UUID> activeUserIdList = ChatUtils.getActiveUserIdList(chat);
        List<MemberEvent> newMemberList = userIdList.stream()
                .filter(id -> !activeUserIdList.contains(id))
                .distinct()
                .map(id -> new MemberEvent(chat, id, MemberEvent.Type.ADD_MEMBER))
                .collect(Collectors.toList());

        memberEventRepository.saveAll(newMemberList);
    }

    public void removeUsersFromChat(UUID chatId, UUID currentUserId, List<UUID> userIdList) {
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(ModelNotFoundException::new);

        permissionService.hasEditMembersPermission(chat, currentUserId);

        List<UUID> activeUserIdList = ChatUtils.getActiveUserIdList(chat);
        List<MemberEvent> memberToDeleteList = userIdList.stream()
                .filter(activeUserIdList::contains)
                .distinct()
                .map(id -> new MemberEvent(chat, id, MemberEvent.Type.DELETE_MEMBER))
                .collect(Collectors.toList());

        memberEventRepository.saveAll(memberToDeleteList);
    }


}
