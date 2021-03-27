package com.persoff68.fatodo.service;

import com.persoff68.fatodo.model.Chat;
import com.persoff68.fatodo.model.Message;
import com.persoff68.fatodo.model.constant.EventMessageType;
import com.persoff68.fatodo.repository.ChatRepository;
import com.persoff68.fatodo.repository.MessageRepository;
import com.persoff68.fatodo.service.exception.ModelNotFoundException;
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
public class SystemMessageService {

    private final MessageRepository messageRepository;
    private final ChatRepository chatRepository;
    private final EntityManager entityManager;
    private final WsService wsService;

    public void createStubMessages(UUID chatId, List<UUID> userIdList) {
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(ModelNotFoundException::new);

        List<Message> messageList = userIdList != null && !userIdList.isEmpty()
                ? userIdList.stream()
                .distinct()
                .map(id -> Message.stub(chat, id))
                .collect(Collectors.toList())
                : Collections.emptyList();

        shortSleep();

        messageRepository.saveAll(messageList);
        messageRepository.flush();
        entityManager.refresh(chat);
    }

    public void createSimpleEventMessage(UUID userId, UUID chatId, EventMessageType type) {
        createEventMessage(userId, chatId, type, Collections.emptyList());
    }

    public void createTextEventMessage(UUID userId, UUID chatId, EventMessageType type, String param) {
        createEventMessage(userId, chatId, type, Collections.singletonList(param));
    }

    public void createIdEventMessage(UUID userId, UUID chatId, EventMessageType type, UUID id) {
        String param = id.toString();
        createEventMessage(userId, chatId, type, Collections.singletonList(param));
    }

    public void createIdListEventMessage(UUID userId, UUID chatId, EventMessageType type, List<UUID> idList) {
        List<String> paramList = idList.stream()
                .map(UUID::toString)
                .collect(Collectors.toList());
        createEventMessage(userId, chatId, type, paramList);
    }

    private void createEventMessage(UUID userId, UUID chatId, EventMessageType type, List<String> paramList) {
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(ModelNotFoundException::new);

        Message message = Message.event(chat, userId, type, paramList);

        shortSleep();

        messageRepository.saveAndFlush(message);
        entityManager.refresh(chat);

        wsService.sendMessageNewEvent(message);
    }

    private void shortSleep() {
        // workaround needed for correct work with message repository
        try {
            Thread.sleep(10);
        } catch (Exception e) {
            // skip if error
        }
    }

}
