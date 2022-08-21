package com.persoff68.fatodo.web.kafka;

import com.persoff68.fatodo.client.WsServiceClient;
import com.persoff68.fatodo.config.annotation.ConditionalOnPropertyNotNull;
import com.persoff68.fatodo.model.dto.WsEventWithUsersDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@ConditionalOnPropertyNotNull(value = "kafka.bootstrapAddress")
public class WsProducer implements WsServiceClient {

    private static final String WS_TOPIC = "ws";

    private final KafkaTemplate<String, WsEventWithUsersDTO> wsKafkaTemplate;

    public void sendEvent(WsEventWithUsersDTO event) {
        wsKafkaTemplate.send(WS_TOPIC, event);
    }

}
