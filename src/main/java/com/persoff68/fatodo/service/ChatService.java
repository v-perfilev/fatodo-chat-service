package com.persoff68.fatodo.service;

import com.persoff68.fatodo.model.Chat;
import com.persoff68.fatodo.model.Message;
import com.persoff68.fatodo.repository.ChatRepository;
import com.persoff68.fatodo.repository.MessageRepository;
import com.persoff68.fatodo.service.exception.ModelNotFoundException;
import com.persoff68.fatodo.service.util.ChatUtils;
import com.persoff68.fatodo.service.util.TimeUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRepository chatRepository;
    private final MessageRepository messageRepository;
    private final UserService userService;
    private final MemberEventService memberEventService;
    private final PermissionService permissionService;

    public Map<Chat, Message> getAllByUserId(UUID userId, Pageable pageable) {
        Page<Message> messagePage = messageRepository.findLastMessagesByUserId(userId, pageable);
        return messagePage.toList().stream()
                .collect(ChatUtils.chatMapCollector);
    }

    public Map<Chat, Message> getAllNewByUserId(UUID userId, Date date) {
        TimeUtils.checkIfOldRequest(date);
        List<Message> messageList = messageRepository.findNewLastMessagesByUserId(userId, date);
        return messageList.stream()
                .collect(ChatUtils.chatMapCollector);
    }

    public Chat getDirectByUserIds(UUID firstUserId, UUID secondUserId) {
        List<UUID> userIdList = List.of(firstUserId, secondUserId);
        Supplier<Chat> createChatSupplier = () -> createDirect(firstUserId, secondUserId);
        return chatRepository.findDirectChat(userIdList)
                .orElseGet(createChatSupplier);
    }

    public Chat getById(UUID userId, UUID chatId) {
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(ModelNotFoundException::new);
        permissionService.hasReadMessagePermission(chat, userId);
        return chat;
    }

    public Chat createDirect(UUID firstUserId, UUID secondUserId) {
        List<UUID> userIdList = List.of(firstUserId, secondUserId);
        return create(userIdList, true);
    }

    public Chat createNonDirect(UUID userId, List<UUID> userIdList) {
        userIdList.add(userId);
        return create(userIdList, false);
    }

    @Transactional
    public void rename(UUID chatId, UUID userId, String title) {
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(ModelNotFoundException::new);
        permissionService.hasEditChatPermission(chat, userId);

        chat.setTitle(title);
        chatRepository.save(chat);
    }

    @Transactional
    protected Chat create(List<UUID> userIdList, boolean isDirect) {
        userService.checkUsersExist(userIdList);
        Chat chat = chatRepository.save(new Chat(isDirect));
        memberEventService.addUsersUnsafe(chat, userIdList);
        return chat;
    }

}
