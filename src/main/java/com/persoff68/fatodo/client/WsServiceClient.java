package com.persoff68.fatodo.client;

import com.persoff68.fatodo.model.dto.ChatDTO;
import com.persoff68.fatodo.model.dto.MessageDTO;
import com.persoff68.fatodo.model.dto.ReactionsDTO;
import com.persoff68.fatodo.model.dto.StatusesDTO;
import com.persoff68.fatodo.model.dto.WsEventDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "ws-service", primary = false)
public interface WsServiceClient {

    @PostMapping(value = "/api/chat/new")
    void sendChatNewEvent(@RequestBody WsEventDTO<ChatDTO> event);

    @PostMapping(value = "/api/chat/update")
    void sendChatUpdateEvent(@RequestBody WsEventDTO<ChatDTO> event);

    @PostMapping(value = "/api/chat/last-message")
    void sendChatLastMessageEvent(@RequestBody WsEventDTO<ChatDTO> event);

    @PostMapping(value = "/api/chat/last-message-update")
    void sendChatLastMessageUpdateEvent(@RequestBody WsEventDTO<ChatDTO> event);

    @PostMapping(value = "/api/chat/message-new")
    void sendMessageNewEvent(@RequestBody WsEventDTO<MessageDTO> event);

    @PostMapping(value = "/api/chat/message-update")
    void sendMessageUpdateEvent(@RequestBody WsEventDTO<MessageDTO> event);

    @PostMapping(value = "/api/chat/statuses")
    void sendStatusesEvent(@RequestBody WsEventDTO<StatusesDTO> event);

    @PostMapping(value = "/api/chat/reactions")
    void sendReactionsEvent(@RequestBody WsEventDTO<ReactionsDTO> event);

}

