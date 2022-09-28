package com.persoff68.fatodo.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.persoff68.fatodo.model.Chat;
import com.persoff68.fatodo.model.Message;
import com.persoff68.fatodo.model.constant.EventMessageType;
import com.persoff68.fatodo.repository.ChatRepository;
import com.persoff68.fatodo.repository.MessageRepository;
import com.persoff68.fatodo.service.exception.ModelNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class SystemMessageService {
    private static final String TYPE_FIELD = "type";
    private static final String TEXT_FIELD = "text";
    private static final String IDS_FIELD = "ids";

    private final MessageRepository messageRepository;
    private final ChatRepository chatRepository;
    private final EntityManager entityManager;
    private final ObjectMapper objectMapper;

    public Message createSimpleEventMessage(UUID userId, UUID chatId, EventMessageType type) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put(TYPE_FIELD, type);
        return createEventMessage(userId, chatId, paramMap, false);
    }

    public Message createTextEventMessage(UUID userId, UUID chatId, EventMessageType type, String text) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put(TYPE_FIELD, type);
        paramMap.put(TEXT_FIELD, text);
        return createEventMessage(userId, chatId, paramMap, false);
    }

    public Message createIdsEventMessage(UUID userId, UUID chatId, EventMessageType type, List<UUID> idList) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put(TYPE_FIELD, type);
        paramMap.put(IDS_FIELD, idList);
        return createEventMessage(userId, chatId, paramMap, false);
    }

    public Message createPrivateEventMessage(UUID userId, UUID chatId, EventMessageType type) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put(TYPE_FIELD, type);
        return createEventMessage(userId, chatId, paramMap, true);
    }

    private Message createEventMessage(UUID userId, UUID chatId, Map<String, Object> paramMap, boolean isPrivate) {
        try {
            Chat chat = chatRepository.findById(chatId)
                    .orElseThrow(ModelNotFoundException::new);

            String params = objectMapper.writeValueAsString(paramMap);
            Message message = isPrivate
                    ? Message.privateEvent(chat, userId, params)
                    : Message.event(chat, userId, params);

            message = messageRepository.saveAndFlush(message);
            entityManager.refresh(chat);

            return message;
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        return null;
    }

}
