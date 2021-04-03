package com.persoff68.fatodo.client;

import com.persoff68.fatodo.model.dto.WsChatEventDTO;
import com.persoff68.fatodo.model.dto.WsMessageEventDTO;
import com.persoff68.fatodo.model.dto.WsReactionsEventDTO;
import com.persoff68.fatodo.model.dto.WsStatusesEventDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "ws-service", primary = false)
public interface WsServiceClient {

    @PostMapping(value = "/api/chat/new")
    void sendChatNewEvent(@RequestBody WsChatEventDTO event);

    @PostMapping(value = "/api/chat/update")
    void sendChatUpdateEvent(@RequestBody WsChatEventDTO event);

    @PostMapping(value = "/api/chat/last-message")
    void sendChatLastMessageEvent(@RequestBody WsChatEventDTO event);

    @PostMapping(value = "/api/chat/last-message-update")
    void sendChatLastMessageUpdateEvent(@RequestBody WsChatEventDTO event);

    @PostMapping(value = "/api/chat/message-new")
    void sendMessageNewEvent(@RequestBody WsMessageEventDTO event);

    @PostMapping(value = "/api/chat/message-update")
    void sendMessageUpdateEvent(@RequestBody WsMessageEventDTO event);

    @PostMapping(value = "/api/chat/statuses")
    void sendStatusesEvent(@RequestBody WsStatusesEventDTO event);

    @PostMapping(value = "/api/chat/reactions")
    void sendReactionsEvent(@RequestBody WsReactionsEventDTO event);

}

