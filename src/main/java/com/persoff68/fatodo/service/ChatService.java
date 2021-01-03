package com.persoff68.fatodo.service;

import com.persoff68.fatodo.model.Chat;
import com.persoff68.fatodo.model.Member;
import com.persoff68.fatodo.repository.MemberRepository;
import com.persoff68.fatodo.repository.ChatRepository;
import com.persoff68.fatodo.service.exception.ModelNotFoundException;
import com.persoff68.fatodo.service.util.PermissionUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRepository chatRepository;
    private final MemberRepository memberRepository;
    private final UserService userService;

    public Chat getDirectChatForUsers(UUID firstUserId, UUID secondUserId) {
        List<UUID> userIdList = List.of(firstUserId, secondUserId);
        return chatRepository.findDirectChat(userIdList)
                .orElse(create(firstUserId, Collections.singletonList(secondUserId), true));
    }

    public Chat create(UUID userId, List<UUID> userIdList, boolean isDirect) {
        userService.checkUsersExist(userIdList);

        Chat chat = chatRepository.save(new Chat(isDirect));

        userIdList.add(userId);
        List<Member> memberList = userIdList.stream()
                .distinct()
                .map(id -> new Member(chat.getId(), id))
                .collect(Collectors.toList());
        chat.setMembers(memberList);

        return chatRepository.save(chat);
    }

    public void rename(UUID chatId, UUID userId, String title) {
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(ModelNotFoundException::new);

        PermissionUtils.checkUserInChat(chat, userId);

        chat.setTitle(title);
        chatRepository.save(chat);
    }

    public void addMembers(UUID chatId, UUID userId, List<UUID> userIdList) {
        userService.checkUsersExist(userIdList);

        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(ModelNotFoundException::new);

        PermissionUtils.checkUserInChat(chat, userId);
        PermissionUtils.checkMemberChangesAllowed(chat);

        List<Member> memberList = chat.getMembers();
        List<UUID> existingMemberUserIdList = memberList.stream()
                .map(Member::getUserId)
                .collect(Collectors.toList());
        List<Member> newMemberList = userIdList.stream()
                .filter(id -> !existingMemberUserIdList.contains(id))
                .distinct()
                .map(id -> new Member(chat.getId(), id))
                .collect(Collectors.toList());

        memberRepository.saveAll(newMemberList);
    }

    public void removeMembers(UUID chatId, UUID userId, List<UUID> userIdList) {
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(ModelNotFoundException::new);

        PermissionUtils.checkUserInChat(chat, userId);
        PermissionUtils.checkMemberChangesAllowed(chat);

        List<Member> memberList = chat.getMembers();
        List<Member> memberToDeleteList = memberList.stream()
                .filter(member -> userIdList.contains(member.getUserId()))
                .collect(Collectors.toList());

        memberRepository.deleteAll(memberToDeleteList);
    }

    public void checkPermission(UUID chatId, UUID userId) {
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(ModelNotFoundException::new);
        PermissionUtils.checkUserInChat(chat, userId);
    }

}
