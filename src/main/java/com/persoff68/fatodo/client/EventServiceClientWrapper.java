package com.persoff68.fatodo.client;

import com.persoff68.fatodo.exception.ClientException;
import com.persoff68.fatodo.model.dto.CreateChatEventDTO;
import com.persoff68.fatodo.model.dto.DeleteUserEventsDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EventServiceClientWrapper implements EventServiceClient {

    @Qualifier("feignEventServiceClient")
    private final EventServiceClient eventServiceClient;

    @Override
    public void addChatEvent(CreateChatEventDTO createChatEventDTO) {
        try {
            eventServiceClient.addChatEvent(createChatEventDTO);
        } catch (Exception e) {
            throw new ClientException();
        }
    }

    @Override
    public void deleteChatEventsForUser(DeleteUserEventsDTO deleteUserEventsDTO) {
        try {
            eventServiceClient.deleteChatEventsForUser(deleteUserEventsDTO);
        } catch (Exception e) {
            throw new ClientException();
        }
    }

}
