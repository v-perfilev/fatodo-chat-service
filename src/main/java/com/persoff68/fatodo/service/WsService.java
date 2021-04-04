package com.persoff68.fatodo.service;

import com.persoff68.fatodo.client.WsServiceClient;
import com.persoff68.fatodo.model.Message;
import com.persoff68.fatodo.model.dto.ChatDTO;
import com.persoff68.fatodo.model.dto.MessageDTO;
import com.persoff68.fatodo.model.dto.ReactionsDTO;
import com.persoff68.fatodo.model.dto.StatusesDTO;
import com.persoff68.fatodo.model.dto.WsChatEventDTO;
import com.persoff68.fatodo.model.dto.WsMessageEventDTO;
import com.persoff68.fatodo.model.dto.WsReactionsEventDTO;
import com.persoff68.fatodo.model.dto.WsStatusesEventDTO;
import com.persoff68.fatodo.model.mapper.ChatMapper;
import com.persoff68.fatodo.model.mapper.MessageMapper;
import com.persoff68.fatodo.service.util.ChatUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WsService {

    private final WsServiceClient wsServiceClient;
    private final ChatMapper chatMapper;
    private final MessageMapper messageMapper;

    public void sendChatLastMessageEvent(Message message) {
        List<UUID> userIdList = ChatUtils.getActiveUserIdList(message.getChat());
        ChatDTO chatDTO = chatMapper.pojoToDTO(message.getChat(), message);
        WsChatEventDTO eventDTO = new WsChatEventDTO(userIdList, chatDTO);
        wsServiceClient.sendChatLastMessageEvent(eventDTO);
    }

    public void sendChatLastMessageUpdateEvent(Message message) {
        List<UUID> userIdList = ChatUtils.getActiveUserIdList(message.getChat());
        ChatDTO chatDTO = chatMapper.pojoToDTO(message.getChat(), message);
        WsChatEventDTO eventDTO = new WsChatEventDTO(userIdList, chatDTO);
        wsServiceClient.sendChatLastMessageUpdateEvent(eventDTO);
    }

    public void sendMessageNewEvent(Message message) {
        List<UUID> userIdList = ChatUtils.getActiveUserIdList(message.getChat());
        MessageDTO messageDTO = messageMapper.pojoToDTO(message);
        WsMessageEventDTO eventDTO = new WsMessageEventDTO(userIdList, messageDTO);
        wsServiceClient.sendMessageNewEvent(eventDTO);
    }

    public void sendMessageUpdateEvent(Message message) {
        List<UUID> userIdList = ChatUtils.getActiveUserIdList(message.getChat());
        MessageDTO messageDTO = messageMapper.pojoToDTO(message);
        WsMessageEventDTO eventDTO = new WsMessageEventDTO(userIdList, messageDTO);
        wsServiceClient.sendMessageUpdateEvent(eventDTO);
    }

    public void sendMessageStatusEvent(Message message) {
        List<UUID> userIdList = ChatUtils.getActiveUserIdList(message.getChat());
        StatusesDTO statusesDTO = messageMapper.pojoToStatusesDTO(message);
        WsStatusesEventDTO eventDTO = new WsStatusesEventDTO(userIdList, statusesDTO);
        wsServiceClient.sendStatusesEvent(eventDTO);
    }

    public void sendMessageReactionEvent(Message message) {
        List<UUID> userIdList = ChatUtils.getActiveUserIdList(message.getChat());
        ReactionsDTO reactionsDTO = messageMapper.pojoToReactionsDTO(message);
        WsReactionsEventDTO eventDTO = new WsReactionsEventDTO(userIdList, reactionsDTO);
        wsServiceClient.sendReactionsEvent(eventDTO);
    }

}
