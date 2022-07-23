package com.persoff68.fatodo.client;

import com.persoff68.fatodo.client.configuration.FeignSystemConfiguration;
import com.persoff68.fatodo.model.dto.CreateChatEventDTO;
import com.persoff68.fatodo.model.dto.DeleteUserEventsDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "event-service", primary = false,
        configuration = {FeignSystemConfiguration.class},
        qualifiers = {"feignEventServiceClient"})
public interface EventServiceClient {

    @PostMapping(value = "/api/event/chat")
    void addChatEvent(@RequestBody CreateChatEventDTO createChatEventDTO);

    @PostMapping("/api/event/chat/delete-users")
    void deleteChatEventsForUser(@RequestBody DeleteUserEventsDTO deleteUserEventsDTO);

}
