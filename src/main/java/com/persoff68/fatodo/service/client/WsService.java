package com.persoff68.fatodo.service.client;

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
import com.persoff68.fatodo.model.dto.WsEventWithUsersDTO;
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

    public void sendChatNewEvent(Chat chat) {
        List<UUID> userIdList = ChatUtils.getActiveUserIdList(chat);
        ChatDTO chatDTO = chatMapper.pojoToDTO(chat);
        WsEventWithUsersDTO eventDTO = new WsEventWithUsersDTO(userIdList, WsEventType.CHAT_CREATE, chatDTO);
        wsServiceClient.sendEvent(eventDTO);
    }

    public void sendChatUpdateEvent(Chat chat) {
        List<UUID> userIdList = ChatUtils.getActiveUserIdList(chat);
        ChatDTO chatDTO = chatMapper.pojoToDTO(chat);
        WsEventWithUsersDTO eventDTO = new WsEventWithUsersDTO(userIdList, WsEventType.CHAT_UPDATE, chatDTO);
        wsServiceClient.sendEvent(eventDTO);
    }

    public void sendChatDeleteEvent(Chat chat, List<UUID> userIdList) {
        ChatDTO chatDTO = chatMapper.pojoToDTO(chat);
        WsEventWithUsersDTO eventDTO = new WsEventWithUsersDTO(userIdList, WsEventType.CHAT_DELETE, chatDTO);
        wsServiceClient.sendEvent(eventDTO);
    }

    public void sendMemberAddEvent(Chat chat, UUID userId) {
        List<UUID> userIdList = ChatUtils.getActiveUserIdList(chat.getMemberEvents());
        ChatMemberDTO chatMemberDTO = new ChatMemberDTO(chat.getId(), userId);
        WsEventWithUsersDTO eventDTO = new WsEventWithUsersDTO(userIdList, WsEventType.CHAT_MEMBER_ADD, chatMemberDTO);
        wsServiceClient.sendEvent(eventDTO);
    }

    public void sendMemberDeleteEvent(Chat chat, UUID userId) {
        List<UUID> userIdList = ChatUtils.getActiveUserIdList(chat.getMemberEvents());
        ChatMemberDTO chatMemberDTO = new ChatMemberDTO(chat.getId(), userId);
        WsEventWithUsersDTO eventDTO = new WsEventWithUsersDTO(userIdList, WsEventType.CHAT_MEMBER_DELETE,
                chatMemberDTO);
        wsServiceClient.sendEvent(eventDTO);
    }

    public void sendMemberLeaveEvent(Chat chat, UUID userId) {
        List<UUID> userIdList = ChatUtils.getActiveUserIdList(chat.getMemberEvents());
        ChatMemberDTO chatMemberDTO = new ChatMemberDTO(chat.getId(), userId);
        WsEventWithUsersDTO eventDTO = new WsEventWithUsersDTO(userIdList, WsEventType.CHAT_MEMBER_LEAVE,
                chatMemberDTO);
        wsServiceClient.sendEvent(eventDTO);
    }

    public void sendMessageNewEvent(Message message) {
        List<UUID> userIdList = ChatUtils.getActiveUserIdList(message.getChat());
        MessageDTO messageDTO = messageMapper.pojoToDTO(message);
        WsEventWithUsersDTO eventDTO = new WsEventWithUsersDTO(userIdList, WsEventType.CHAT_MESSAGE_CREATE, messageDTO);
        wsServiceClient.sendEvent(eventDTO);
    }

    public void sendMessageUpdateEvent(Message message, boolean isLastMessage) {
        List<UUID> userIdList = ChatUtils.getActiveUserIdList(message.getChat());
        MessageDTO messageDTO = messageMapper.pojoToDTO(message);
        messageDTO.setLastMessage(isLastMessage);
        WsEventWithUsersDTO eventDTO = new WsEventWithUsersDTO(userIdList, WsEventType.CHAT_MESSAGE_UPDATE, messageDTO);
        wsServiceClient.sendEvent(eventDTO);
    }

    public void sendMessageReactionEvent(Reaction reaction, Chat chat) {
        ReactionDTO reactionDTO = reactionMapper.pojoToDTO(reaction, chat.getId());
        WsEventWithUsersDTO eventDTO = new WsEventWithUsersDTO(null, WsEventType.CHAT_REACTION, reactionDTO);
        wsServiceClient.sendEvent(eventDTO);
    }

    public void sendMessageReactionIncomingEvent(Reaction reaction, Chat chat, UUID userId) {
        List<UUID> userIdList = List.of(userId);
        ReactionDTO reactionDTO = reactionMapper.pojoToDTO(reaction, chat.getId());
        WsEventWithUsersDTO eventDTO = new WsEventWithUsersDTO(userIdList,
                WsEventType.CHAT_REACTION_INCOMING, reactionDTO);
        wsServiceClient.sendEvent(eventDTO);
    }

    public void sendMessageStatusEvent(Status status) {
//        List<UUID> userIdList = ChatUtils.getActiveUserIdList(message.getChat());
        StatusDTO statusDTO = null; // TODO
        WsEventWithUsersDTO eventDTO = new WsEventWithUsersDTO(null, WsEventType.CHAT_STATUS, statusDTO);
        wsServiceClient.sendEvent(eventDTO);
    }

}
