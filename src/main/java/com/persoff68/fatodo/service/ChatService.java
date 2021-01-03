package com.persoff68.fatodo.service;

import com.persoff68.fatodo.model.Chat;
import com.persoff68.fatodo.model.Member;
import com.persoff68.fatodo.repository.ChatRepository;
import com.persoff68.fatodo.service.exception.ModelNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRepository chatRepository;
    private final UserService userService;
    private final PermissionService permissionService;

    public Page<Chat> getAllByUserId(UUID userId, Pageable pageable) {
        return chatRepository.findAllByUser(userId, pageable);
//        return chatRepository.findAllByUser(userId);
    }

    public Chat getById(UUID chatId) {
        return chatRepository.findById(chatId)
                .orElseThrow(ModelNotFoundException::new);
    }

    public Chat getDirectByUserIds(UUID firstUserId, UUID secondUserId) {
        List<UUID> userIdList = List.of(firstUserId, secondUserId);
        List<UUID> secondUserIdList = Collections.singletonList(secondUserId);
        Supplier<Chat> createChatSupplier = () -> create(firstUserId, secondUserIdList, true);
        return chatRepository.findDirectChat(userIdList)
                .orElseGet(createChatSupplier);
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

        permissionService.hasEditChatPermission(chat, userId);

        chat.setTitle(title);
        chatRepository.save(chat);
    }

}