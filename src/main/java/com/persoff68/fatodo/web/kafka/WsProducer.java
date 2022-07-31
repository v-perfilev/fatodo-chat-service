package com.persoff68.fatodo.web.kafka;

import com.persoff68.fatodo.client.WsServiceClient;
import com.persoff68.fatodo.config.annotation.ConditionalOnPropertyNotNull;
import com.persoff68.fatodo.config.constant.KafkaTopics;
import com.persoff68.fatodo.model.dto.ChatDTO;
import com.persoff68.fatodo.model.dto.ClearEventDTO;
import com.persoff68.fatodo.model.dto.MessageDTO;
import com.persoff68.fatodo.model.dto.ReactionsDTO;
import com.persoff68.fatodo.model.dto.StatusesDTO;
import com.persoff68.fatodo.model.dto.WsEventDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@ConditionalOnPropertyNotNull(value = "kafka.bootstrapAddress")
public class WsProducer implements WsServiceClient {

    private final KafkaTemplate<String, WsEventDTO<ChatDTO>> wsEventChatKafkaTemplate;
    private final KafkaTemplate<String, WsEventDTO<MessageDTO>> wsEventMessageKafkaTemplate;
    private final KafkaTemplate<String, WsEventDTO<StatusesDTO>> wsEventStatusesKafkaTemplate;
    private final KafkaTemplate<String, WsEventDTO<ReactionsDTO>> wsEventReactionsKafkaTemplate;
    private final KafkaTemplate<String, WsEventDTO<ClearEventDTO>> wsClearEventKafkaTemplate;

    public void sendChatNewEvent(WsEventDTO<ChatDTO> event) {
        wsEventChatKafkaTemplate.send(KafkaTopics.WS_CHAT.getValue(), "new", event);
    }

    public void sendChatUpdateEvent(WsEventDTO<ChatDTO> event) {
        wsEventChatKafkaTemplate.send(KafkaTopics.WS_CHAT.getValue(), "update", event);
    }

    public void sendChatLastMessageEvent(WsEventDTO<ChatDTO> event) {
        wsEventChatKafkaTemplate.send(KafkaTopics.WS_CHAT.getValue(), "last-message", event);
    }

    public void sendChatLastMessageUpdateEvent(WsEventDTO<ChatDTO> event) {
        wsEventChatKafkaTemplate.send(KafkaTopics.WS_CHAT.getValue(), "last-message-update", event);
    }

    public void sendMessageNewEvent(WsEventDTO<MessageDTO> event) {
        wsEventMessageKafkaTemplate.send(KafkaTopics.WS_CHAT.getValue(), "message-new", event);
    }

    public void sendMessageUpdateEvent(WsEventDTO<MessageDTO> event) {
        wsEventMessageKafkaTemplate.send(KafkaTopics.WS_CHAT.getValue(), "message-update", event);
    }

    public void sendStatusesEvent(WsEventDTO<StatusesDTO> event) {
        wsEventStatusesKafkaTemplate.send(KafkaTopics.WS_CHAT.getValue(), "statuses", event);
    }

    public void sendReactionsEvent(WsEventDTO<ReactionsDTO> event) {
        wsEventReactionsKafkaTemplate.send(KafkaTopics.WS_CHAT.getValue(), "reactions", event);
    }

    public void sendClearEvent(WsEventDTO<ClearEventDTO> event) {
        wsClearEventKafkaTemplate.send(KafkaTopics.WS_CLEAR.getValue(), event);
    }

}
