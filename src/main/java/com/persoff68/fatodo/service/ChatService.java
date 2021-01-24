package com.persoff68.fatodo.service;

import com.persoff68.fatodo.model.Chat;
import com.persoff68.fatodo.model.MemberEvent;
import com.persoff68.fatodo.model.Message;
import com.persoff68.fatodo.repository.ChatRepository;
import com.persoff68.fatodo.repository.MessageRepository;
import com.persoff68.fatodo.service.exception.ModelNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final Collector<Message, ?, Map<Chat, Message>> chatMapCollector =
            Collectors.toMap(
                    Message::getChat,
                    message -> message,
                    (e1, e2) -> e1,
                    LinkedHashMap::new
            );

    private final ChatRepository chatRepository;
    private final MessageRepository messageRepository;
    private final UserService userService;
    private final PermissionService permissionService;

    public Map<Chat, Message> getAllByUserId(UUID userId, Pageable pageable) {
        Page<Message> messagePage = messageRepository.findLastMessagesByUserId(userId, pageable);
        List<Message> messageList = messagePage.toList();
        return messageList.stream()
                .collect(chatMapCollector);
    }

    public Map<Chat, Message> getAllNewByUserId(UUID userId, Date date) {
        List<Message> messageList = messageRepository.findNewLastMessagesByUserId(userId, date);
        return messageList.stream()
                .collect(chatMapCollector);
    }

    public Chat getDirectByUserIds(UUID firstUserId, UUID secondUserId) {
        List<UUID> userIdList = List.of(firstUserId, secondUserId);
        Supplier<Chat> createChatSupplier = () -> createDirect(firstUserId, secondUserId);
        return chatRepository.findDirectChat(userIdList)
                .orElseGet(createChatSupplier);
    }

    public Chat getById(UUID chatId) {
        return chatRepository.findById(chatId)
                .orElseThrow(ModelNotFoundException::new);
    }

    public Chat createDirect(UUID firstUserId, UUID secondUserId) {
        userService.checkUserExists(secondUserId);
        List<UUID> userIdList = List.of(firstUserId, secondUserId);
        return create(userIdList, true);
    }

    public Chat createNonDirect(UUID userId, List<UUID> userIdList) {
        userService.checkUsersExist(userIdList);
        userIdList.add(userId);
        return create(userIdList, false);
    }

    public void rename(UUID chatId, UUID userId, String title) {
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(ModelNotFoundException::new);

        permissionService.hasEditChatPermission(chat, userId);

        chat.setTitle(title);
        chatRepository.save(chat);
    }

    private Chat create(List<UUID> userIdList, boolean isDirect) {
        Chat chat = chatRepository.save(new Chat(isDirect));

        List<MemberEvent> memberList = userIdList.stream()
                .distinct()
                .map(id -> new MemberEvent(chat, id, MemberEvent.Type.ADD_MEMBER))
                .collect(Collectors.toList());
        chat.setMemberEvents(memberList);

        return chatRepository.save(chat);
    }

}
