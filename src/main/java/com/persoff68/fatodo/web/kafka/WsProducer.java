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

    private final KafkaTemplate<String, WsEventDTO<ChatDTO>> wsEventChatKafkaTemplate;
    private final KafkaTemplate<String, WsEventDTO<MessageDTO>> wsEventMessageKafkaTemplate;
    private final KafkaTemplate<String, WsEventDTO<StatusesDTO>> wsEventStatusesKafkaTemplate;
    private final KafkaTemplate<String, WsEventDTO<ReactionsDTO>> wsEventReactionsKafkaTemplate;

    public void sendChatNewEvent(WsEventDTO<ChatDTO> event) {
        wsEventChatKafkaTemplate.send("ws_chat", "new", event);
    }

    public void sendChatUpdateEvent(WsEventDTO<ChatDTO> event) {
        wsEventChatKafkaTemplate.send("ws_chat", "update", event);
    }

    public void sendChatLastMessageEvent(WsEventDTO<ChatDTO> event) {
        wsEventChatKafkaTemplate.send("ws_chat", "last-message-new", event);
    }

    public void sendChatLastMessageUpdateEvent(WsEventDTO<ChatDTO> event) {
        wsEventChatKafkaTemplate.send("ws_chat", "last-message-update", event);
    }

    public void sendMessageNewEvent(WsEventDTO<MessageDTO> event) {
        wsEventMessageKafkaTemplate.send("ws_chat", "message-new", event);
    }

    public void sendMessageUpdateEvent(WsEventDTO<MessageDTO> event) {
        wsEventMessageKafkaTemplate.send("ws_chat", "message-update", event);
    }

    public void sendStatusesEvent(WsEventDTO<StatusesDTO> event) {
        wsEventStatusesKafkaTemplate.send("ws_chat", "statuses", event);
    }

    public void sendReactionsEvent(WsEventDTO<ReactionsDTO> event) {
        wsEventReactionsKafkaTemplate.send("ws_chat", "reactions", event);
    }

}
