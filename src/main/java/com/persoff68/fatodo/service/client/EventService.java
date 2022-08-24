package com.persoff68.fatodo.service.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.persoff68.fatodo.client.EventServiceClient;
import com.persoff68.fatodo.mapper.ChatMapper;
import com.persoff68.fatodo.mapper.ReactionMapper;
import com.persoff68.fatodo.model.Chat;
import com.persoff68.fatodo.model.Reaction;
import com.persoff68.fatodo.model.constant.EventType;
import com.persoff68.fatodo.model.dto.ChatDTO;
import com.persoff68.fatodo.model.dto.ChatMemberDTO;
import com.persoff68.fatodo.model.dto.ReactionDTO;
import com.persoff68.fatodo.model.dto.event.EventDTO;
import com.persoff68.fatodo.service.exception.ModelInvalidException;
import com.persoff68.fatodo.service.util.ChatUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Async
public class EventService {

    private final EventServiceClient eventServiceClient;
    private final ChatMapper chatMapper;
    private final ReactionMapper reactionMapper;
    private final ObjectMapper objectMapper;

    public void sendChatNewEvent(Chat chat) {
        List<UUID> userIdList = ChatUtils.getActiveUserIdList(chat);
        ChatDTO chatDTO = chatMapper.pojoToDTO(chat);
        String payload = serialize(chatDTO);
        EventDTO eventDTO = new EventDTO(userIdList, EventType.CHAT_CREATE, payload, chat.getCreatedBy());
        eventServiceClient.addEvent(eventDTO);
    }

    public void sendChatUpdateEvent(Chat chat) {
        List<UUID> userIdList = ChatUtils.getActiveUserIdList(chat);
        ChatDTO chatDTO = chatMapper.pojoToDTO(chat);
        String payload = serialize(chatDTO);
        EventDTO eventDTO = new EventDTO(userIdList, EventType.CHAT_UPDATE, payload, chat.getLastModifiedBy());
        eventServiceClient.addEvent(eventDTO);
    }

    public void sendMemberAddEvent(Chat chat, List<UUID> memberIdList, UUID userId) {
        List<UUID> userIdList = ChatUtils.getActiveUserIdList(chat.getMemberEvents());
        List<ChatMemberDTO> chatMemberDTOList = memberIdList.stream()
                .map(memberId -> new ChatMemberDTO(chat.getId(), memberId))
                .toList();
        String payload = serialize(chatMemberDTOList);
        EventDTO eventDTO = new EventDTO(userIdList, EventType.CHAT_MEMBER_ADD, payload, userId);
        eventServiceClient.addEvent(eventDTO);
    }

    public void sendMemberDeleteEvent(Chat chat, List<UUID> memberIdList, UUID userId) {
        List<UUID> userIdList = ChatUtils.getActiveUserIdList(chat.getMemberEvents());
        List<ChatMemberDTO> chatMemberDTOList = memberIdList.stream()
                .map(memberId -> new ChatMemberDTO(chat.getId(), memberId))
                .toList();
        String payload = serialize(chatMemberDTOList);
        EventDTO eventDTO = new EventDTO(userIdList, EventType.CHAT_MEMBER_DELETE, payload, userId);
        eventServiceClient.addEvent(eventDTO);
    }

    public void sendMemberLeaveEvent(Chat chat, UUID userId) {
        List<UUID> userIdList = ChatUtils.getActiveUserIdList(chat.getMemberEvents());
        ChatMemberDTO chatMemberDTO = new ChatMemberDTO(chat.getId(), userId);
        String payload = serialize(chatMemberDTO);
        EventDTO eventDTO = new EventDTO(userIdList, EventType.CHAT_MEMBER_LEAVE, payload, userId);
        eventServiceClient.addEvent(eventDTO);
    }

    public void sendMessageReactionIncomingEvent(Reaction reaction) {
        List<UUID> userIdList = List.of(reaction.getMessage().getUserId());
        ReactionDTO reactionDTO = reactionMapper.pojoToDTO(reaction, reaction.getMessage().getChat().getId());
        String payload = serialize(reactionDTO);
        EventDTO eventDTO = new EventDTO(userIdList, EventType.CHAT_REACTION_INCOMING, payload, reaction.getUserId());
        eventServiceClient.addEvent(eventDTO);
    }

    private String serialize(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new ModelInvalidException();
        }
    }

}
