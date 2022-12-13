package com.persoff68.fatodo.service.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.persoff68.fatodo.client.WsServiceClient;
import com.persoff68.fatodo.mapper.ChatMapper;
import com.persoff68.fatodo.mapper.MessageMapper;
import com.persoff68.fatodo.mapper.ReactionMapper;
import com.persoff68.fatodo.mapper.StatusMapper;
import com.persoff68.fatodo.model.Chat;
import com.persoff68.fatodo.model.Message;
import com.persoff68.fatodo.model.Reaction;
import com.persoff68.fatodo.model.Status;
import com.persoff68.fatodo.model.constant.WsEventType;
import com.persoff68.fatodo.model.dto.ChatDTO;
import com.persoff68.fatodo.model.dto.ChatMemberDTO;
import com.persoff68.fatodo.model.dto.MessageDTO;
import com.persoff68.fatodo.model.dto.ReactionDTO;
import com.persoff68.fatodo.model.dto.StatusDTO;
import com.persoff68.fatodo.model.dto.event.WsEventDTO;
import com.persoff68.fatodo.service.exception.ModelInvalidException;
import com.persoff68.fatodo.service.util.ChatUtils;
import lombok.RequiredArgsConstructor;
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
    private final ReactionMapper reactionMapper;
    private final StatusMapper statusMapper;
    private final ObjectMapper objectMapper;

    public void sendChatNewEvent(Chat chat, Message message, UUID userId) {
        List<UUID> userIdList = ChatUtils.getActiveUserIdList(chat);
        ChatDTO chatDTO = chatMapper.pojoToDTO(chat, message);
        String payload = serialize(chatDTO);
        WsEventDTO eventDTO = new WsEventDTO(userIdList, WsEventType.CHAT_CREATE, payload, userId);
        wsServiceClient.sendEvent(eventDTO);
    }

    public void sendChatUpdateEvent(Chat chat, Message message, UUID userId) {
        List<UUID> userIdList = ChatUtils.getActiveUserIdList(chat);
        ChatDTO chatDTO = chatMapper.pojoToDTO(chat, message);
        String payload = serialize(chatDTO);
        WsEventDTO eventDTO = new WsEventDTO(userIdList, WsEventType.CHAT_UPDATE, payload, userId);
        wsServiceClient.sendEvent(eventDTO);
    }

    public void sendMemberAddEvent(Chat chat, List<UUID> memberIdList, UUID userId) {
        List<UUID> userIdList = ChatUtils.getActiveUserIdList(chat.getMemberEvents());
        List<ChatMemberDTO> chatMemberDTOList = memberIdList.stream()
                .map(memberId -> new ChatMemberDTO(chat.getId(), memberId))
                .toList();
        String payload = serialize(chatMemberDTOList);
        WsEventDTO eventDTO = new WsEventDTO(userIdList, WsEventType.CHAT_MEMBER_ADD, payload, userId);
        wsServiceClient.sendEvent(eventDTO);
    }

    public void sendMemberDeleteEvent(Chat chat, List<UUID> memberIdList, UUID userId) {
        List<UUID> userIdList = ChatUtils.getActiveUserIdList(chat.getMemberEvents());
        List<ChatMemberDTO> chatMemberDTOList = memberIdList.stream()
                .map(memberId -> new ChatMemberDTO(chat.getId(), memberId))
                .toList();
        String payload = serialize(chatMemberDTOList);
        WsEventDTO eventDTO = new WsEventDTO(userIdList, WsEventType.CHAT_MEMBER_DELETE, payload, userId);
        wsServiceClient.sendEvent(eventDTO);
    }

    public void sendMemberLeaveEvent(Chat chat, UUID userId) {
        List<UUID> userIdList = ChatUtils.getActiveUserIdList(chat.getMemberEvents());
        ChatMemberDTO chatMemberDTO = new ChatMemberDTO(chat.getId(), userId);
        String payload = serialize(chatMemberDTO);
        WsEventDTO eventDTO = new WsEventDTO(userIdList, WsEventType.CHAT_MEMBER_LEAVE, payload, userId);
        wsServiceClient.sendEvent(eventDTO);
    }

    public void sendMessageNewEvent(Message message, UUID userId) {
        List<UUID> userIdList = ChatUtils.getActiveUserIdList(message.getChat());
        MessageDTO messageDTO = messageMapper.pojoToDTO(message);
        String payload = serialize(messageDTO);
        WsEventDTO eventDTO = new WsEventDTO(userIdList, WsEventType.CHAT_MESSAGE_CREATE, payload, userId);
        wsServiceClient.sendEvent(eventDTO);
    }

    public void sendMessageUpdateEvent(Message message, UUID userId) {
        List<UUID> userIdList = ChatUtils.getActiveUserIdList(message.getChat());
        MessageDTO messageDTO = messageMapper.pojoToDTO(message);
        String payload = serialize(messageDTO);
        WsEventDTO eventDTO = new WsEventDTO(userIdList, WsEventType.CHAT_MESSAGE_UPDATE, payload, userId);
        wsServiceClient.sendEvent(eventDTO);
    }

    public void sendMessageReactionEvent(Reaction reaction, UUID userId) {
        List<UUID> userIdList = ChatUtils.getActiveUserIdList(reaction.getMessage().getChat());
        ReactionDTO reactionDTO = reactionMapper.pojoToDTO(reaction);
        String payload = serialize(reactionDTO);
        WsEventDTO eventDTO = new WsEventDTO(userIdList, WsEventType.CHAT_REACTION, payload, userId);
        wsServiceClient.sendEvent(eventDTO);
    }

    public void sendMessageReactionIncomingEvent(Reaction reaction, UUID userId) {
        List<UUID> userIdList = List.of(reaction.getMessage().getUserId());
        ReactionDTO reactionDTO = reactionMapper.pojoToDTO(reaction);
        String payload = serialize(reactionDTO);
        WsEventDTO eventDTO = new WsEventDTO(userIdList, WsEventType.CHAT_REACTION_INCOMING, payload, userId);
        wsServiceClient.sendEvent(eventDTO);
    }

    public void sendMessageStatusEvent(Status status, UUID userId) {
        Chat chat = status.getMessage().getChat();
        List<UUID> userIdList = ChatUtils.getActiveUserIdList(chat);
        StatusDTO statusDTO = statusMapper.pojoToDTO(status);
        String payload = serialize(statusDTO);
        WsEventDTO eventDTO = new WsEventDTO(userIdList, WsEventType.CHAT_STATUS, payload, userId);
        wsServiceClient.sendEvent(eventDTO);
    }

    private String serialize(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new ModelInvalidException();
        }
    }

}
