package com.persoff68.fatodo.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.persoff68.fatodo.model.Chat;
import com.persoff68.fatodo.model.Message;
import com.persoff68.fatodo.model.constant.EventMessageType;
import com.persoff68.fatodo.repository.ChatRepository;
import com.persoff68.fatodo.repository.MessageRepository;
import com.persoff68.fatodo.service.exception.ModelNotFoundException;
import com.persoff68.fatodo.service.client.WsService;
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
    private final WsService wsService;

    public void createSimpleEventMessage(UUID userId, UUID chatId, EventMessageType type) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put(TYPE_FIELD, type);
        createEventMessage(userId, chatId, paramMap, false);
    }

    public void createTextEventMessage(UUID userId, UUID chatId, EventMessageType type, String text) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put(TYPE_FIELD, type);
        paramMap.put(TEXT_FIELD, text);
        createEventMessage(userId, chatId, paramMap, false);
    }

    public void createIdsEventMessage(UUID userId, UUID chatId, EventMessageType type, List<UUID> idList) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put(TYPE_FIELD, type);
        paramMap.put(IDS_FIELD, idList);
        createEventMessage(userId, chatId, paramMap, false);
    }

    public void createPrivateEventMessage(UUID userId, UUID chatId, EventMessageType type) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put(TYPE_FIELD, type);
        createEventMessage(userId, chatId, paramMap, true);
    }

    private void createEventMessage(UUID userId, UUID chatId, Map<String, Object> paramMap, boolean isPrivate) {
        try {
            Chat chat = chatRepository.findById(chatId)
                    .orElseThrow(ModelNotFoundException::new);

            String params = objectMapper.writeValueAsString(paramMap);
            Message message = isPrivate
                    ? Message.privateEvent(chat, userId, params)
                    : Message.event(chat, userId, params);

            messageRepository.saveAndFlush(message);
            entityManager.refresh(chat);

            // WS
            wsService.sendMessageNewEvent(message);
            wsService.sendChatLastMessageEvent(message);

        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

}
