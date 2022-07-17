package com.persoff68.fatodo.service.client;

import com.persoff68.fatodo.client.WsServiceClient;
import com.persoff68.fatodo.mapper.ChatMapper;
import com.persoff68.fatodo.mapper.MessageMapper;
import com.persoff68.fatodo.model.Chat;
import com.persoff68.fatodo.model.Message;
import com.persoff68.fatodo.model.dto.ChatDTO;
import com.persoff68.fatodo.model.dto.MessageDTO;
import com.persoff68.fatodo.model.dto.ReactionsDTO;
import com.persoff68.fatodo.model.dto.StatusesDTO;
import com.persoff68.fatodo.model.dto.WsEventDTO;
import com.persoff68.fatodo.service.util.ChatUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class WsService {

    private final WsServiceClient wsServiceClient;
    private final ChatMapper chatMapper;
    private final MessageMapper messageMapper;

    public void sendChatNewEvent(Chat chat) {
        List<UUID> userIdList = ChatUtils.getActiveUserIdList(chat);
        ChatDTO chatDTO = chatMapper.pojoToDTO(chat);
        WsEventDTO<ChatDTO> eventDTO = new WsEventDTO<>(userIdList, chatDTO);
        sendChatNewEventAsync(eventDTO);
    }

    public void sendChatUpdateEvent(Chat chat) {
        List<UUID> userIdList = ChatUtils.getActiveUserIdList(chat);
        ChatDTO chatDTO = chatMapper.pojoToDTO(chat);
        WsEventDTO<ChatDTO> eventDTO = new WsEventDTO<>(userIdList, chatDTO);
        sendChatUpdateEventAsync(eventDTO);
    }

    public void sendChatLastMessageEvent(Message message) {
        List<UUID> userIdList = ChatUtils.getActiveUserIdList(message.getChat());
        ChatDTO chatDTO = chatMapper.pojoToDTO(message.getChat(), message);
        WsEventDTO<ChatDTO> eventDTO = new WsEventDTO<>(userIdList, chatDTO);
        sendChatLastMessageEventAsync(eventDTO);
    }

    public void sendChatLastMessageUpdateEvent(Message message) {
        List<UUID> userIdList = ChatUtils.getActiveUserIdList(message.getChat());
        ChatDTO chatDTO = chatMapper.pojoToDTO(message.getChat(), message);
        WsEventDTO<ChatDTO> eventDTO = new WsEventDTO<>(userIdList, chatDTO);
        sendChatLastMessageUpdateEventAsync(eventDTO);
    }

    public void sendMessageNewEvent(Message message) {
        List<UUID> userIdList = ChatUtils.getActiveUserIdList(message.getChat());
        MessageDTO messageDTO = messageMapper.pojoToDTO(message);
        WsEventDTO<MessageDTO> eventDTO = new WsEventDTO<>(userIdList, messageDTO);
        sendMessageNewEventAsync(eventDTO);
    }

    public void sendMessageUpdateEvent(Message message) {
        List<UUID> userIdList = ChatUtils.getActiveUserIdList(message.getChat());
        MessageDTO messageDTO = messageMapper.pojoToDTO(message);
        WsEventDTO<MessageDTO> eventDTO = new WsEventDTO<>(userIdList, messageDTO);
        sendMessageUpdateEventAsync(eventDTO);
    }

    public void sendMessageStatusEvent(Message message) {
        List<UUID> userIdList = ChatUtils.getActiveUserIdList(message.getChat());
        StatusesDTO statusesDTO = messageMapper.pojoToStatusesDTO(message);
        WsEventDTO<StatusesDTO> eventDTO = new WsEventDTO<>(userIdList, statusesDTO);
        sendStatusesEventAsync(eventDTO);
    }

    public void sendMessageReactionEvent(Message message) {
        List<UUID> userIdList = ChatUtils.getActiveUserIdList(message.getChat());
        ReactionsDTO reactionsDTO = messageMapper.pojoToReactionsDTO(message);
        WsEventDTO<ReactionsDTO> eventDTO = new WsEventDTO<>(userIdList, reactionsDTO);
        sendReactionsEventAsync(eventDTO);
    }

    @Async
    public void sendChatNewEventAsync(WsEventDTO<ChatDTO> event) {
        wsServiceClient.sendChatNewEvent(event);
    }

    @Async
    public void sendChatUpdateEventAsync(WsEventDTO<ChatDTO> event) {
        wsServiceClient.sendChatUpdateEvent(event);
    }

    @Async
    public void sendChatLastMessageEventAsync(WsEventDTO<ChatDTO> event) {
        wsServiceClient.sendChatLastMessageEvent(event);
    }

    @Async
    public void sendChatLastMessageUpdateEventAsync(WsEventDTO<ChatDTO> event) {
        wsServiceClient.sendChatLastMessageUpdateEvent(event);
    }

    @Async
    public void sendMessageNewEventAsync(WsEventDTO<MessageDTO> event) {
        wsServiceClient.sendMessageNewEvent(event);
    }

    @Async
    public void sendMessageUpdateEventAsync(WsEventDTO<MessageDTO> event) {
        wsServiceClient.sendMessageUpdateEvent(event);
    }

    @Async
    public void sendStatusesEventAsync(WsEventDTO<StatusesDTO> event) {
        wsServiceClient.sendStatusesEvent(event);
    }

    @Async
    public void sendReactionsEventAsync(WsEventDTO<ReactionsDTO> event) {
        wsServiceClient.sendReactionsEvent(event);
    }
}
