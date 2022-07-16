package com.persoff68.fatodo.web.kafka;

import com.persoff68.fatodo.client.EventServiceClient;
import com.persoff68.fatodo.config.annotation.ConditionalOnPropertyNotNull;
import com.persoff68.fatodo.config.constant.KafkaTopics;
import com.persoff68.fatodo.model.dto.CreateChatEventDTO;
import com.persoff68.fatodo.model.dto.DeleteUserEventsDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@ConditionalOnPropertyNotNull(value = "kafka.bootstrapAddress")
public class EventProducer implements EventServiceClient {

    private final KafkaTemplate<String, CreateChatEventDTO> eventChatKafkaTemplate;
    private final KafkaTemplate<String, DeleteUserEventsDTO> eventDeleteUserKafkaTemplate;

    @Override
    public void addChatEvent(CreateChatEventDTO createChatEventDTO) {
        eventChatKafkaTemplate.send(KafkaTopics.EVENT_ADD.getValue(), "chat", createChatEventDTO);
    }

    @Override
    public void deleteChatEventsForUser(DeleteUserEventsDTO deleteUserEventsDTO) {
        eventDeleteUserKafkaTemplate.send(KafkaTopics.EVENT_DELETE.getValue(), "chat-delete-users",
                deleteUserEventsDTO);
    }

}
