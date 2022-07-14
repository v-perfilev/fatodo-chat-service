package com.persoff68.fatodo.web.kafka;

import com.persoff68.fatodo.client.WsServiceClient;
import com.persoff68.fatodo.config.annotation.ConditionalOnPropertyNotNull;
import com.persoff68.fatodo.model.dto.ChatDTO;
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

    private static final String WS_CHAT_TOPIC = "ws_chat";

    private final KafkaTemplate<String, WsEventDTO<ChatDTO>> wsEventChatKafkaTemplate;
    private final KafkaTemplate<String, WsEventDTO<MessageDTO>> wsEventMessageKafkaTemplate;
    private final KafkaTemplate<String, WsEventDTO<StatusesDTO>> wsEventStatusesKafkaTemplate;
    private final KafkaTemplate<String, WsEventDTO<ReactionsDTO>> wsEventReactionsKafkaTemplate;

    public void sendChatNewEvent(WsEventDTO<ChatDTO> event) {
        wsEventChatKafkaTemplate.send(WS_CHAT_TOPIC, "new", event);
    }

    public void sendChatUpdateEvent(WsEventDTO<ChatDTO> event) {
        wsEventChatKafkaTemplate.send(WS_CHAT_TOPIC, "update", event);
    }

    public void sendChatLastMessageEvent(WsEventDTO<ChatDTO> event) {
        wsEventChatKafkaTemplate.send(WS_CHAT_TOPIC, "last-message", event);
    }

    public void sendChatLastMessageUpdateEvent(WsEventDTO<ChatDTO> event) {
        wsEventChatKafkaTemplate.send(WS_CHAT_TOPIC, "last-message-update", event);
    }

    public void sendMessageNewEvent(WsEventDTO<MessageDTO> event) {
        wsEventMessageKafkaTemplate.send(WS_CHAT_TOPIC, "message-new", event);
    }

    public void sendMessageUpdateEvent(WsEventDTO<MessageDTO> event) {
        wsEventMessageKafkaTemplate.send(WS_CHAT_TOPIC, "message-update", event);
    }

    public void sendStatusesEvent(WsEventDTO<StatusesDTO> event) {
        wsEventStatusesKafkaTemplate.send(WS_CHAT_TOPIC, "statuses", event);
    }

    public void sendReactionsEvent(WsEventDTO<ReactionsDTO> event) {
        wsEventReactionsKafkaTemplate.send(WS_CHAT_TOPIC, "reactions", event);
    }

}
