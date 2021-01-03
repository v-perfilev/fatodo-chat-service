package com.persoff68.fatodo.service;

import com.persoff68.fatodo.model.Chat;
import com.persoff68.fatodo.model.Member;
import com.persoff68.fatodo.repository.ChatRepository;
import com.persoff68.fatodo.repository.MemberRepository;
import com.persoff68.fatodo.service.exception.ModelNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final ChatRepository chatRepository;
    private final MemberRepository memberRepository;
    private final UserService userService;
    private final PermissionService permissionService;

    public void addToChat(UUID chatId, UUID userId, List<UUID> userIdList) {
        userService.checkUsersExist(userIdList);

        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(ModelNotFoundException::new);

        permissionService.hasEditMembersPermission(chat, userId);

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

    public void removeFromChat(UUID chatId, UUID userId, List<UUID> userIdList) {
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(ModelNotFoundException::new);

        permissionService.hasEditMembersPermission(chat, userId);

        List<Member> memberList = chat.getMembers();
        List<Member> memberToDeleteList = memberList.stream()
                .filter(member -> userIdList.contains(member.getUserId()))
                .collect(Collectors.toList());

        memberRepository.deleteAll(memberToDeleteList);
    }


}
