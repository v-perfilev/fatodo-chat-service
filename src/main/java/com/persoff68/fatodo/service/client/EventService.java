package com.persoff68.fatodo.service.client;

import com.persoff68.fatodo.client.EventServiceClient;
import com.persoff68.fatodo.mapper.ChatMapper;
import com.persoff68.fatodo.mapper.ReactionMapper;
import com.persoff68.fatodo.model.Chat;
import com.persoff68.fatodo.model.Reaction;
import com.persoff68.fatodo.model.constant.EventType;
import com.persoff68.fatodo.model.dto.ChatDTO;
import com.persoff68.fatodo.model.dto.ChatMemberDTO;
import com.persoff68.fatodo.model.dto.EventDTO;
import com.persoff68.fatodo.model.dto.ReactionDTO;
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

    public void sendChatNewEvent(Chat chat) {
        List<UUID> userIdList = ChatUtils.getActiveUserIdList(chat);
        ChatDTO chatDTO = chatMapper.pojoToDTO(chat);
        EventDTO eventDTO = new EventDTO(userIdList, EventType.CHAT_CREATE, chatDTO, chat.getCreatedBy());
        eventServiceClient.addEvent(eventDTO);
    }

    public void sendChatUpdateEvent(Chat chat) {
        List<UUID> userIdList = ChatUtils.getActiveUserIdList(chat);
        ChatDTO chatDTO = chatMapper.pojoToDTO(chat);
        EventDTO eventDTO = new EventDTO(userIdList, EventType.CHAT_UPDATE, chatDTO, chat.getLastModifiedBy());
        eventServiceClient.addEvent(eventDTO);
    }

    public void sendMemberAddEvent(Chat chat, List<UUID> memberIdList, UUID userId) {
        List<UUID> userIdList = ChatUtils.getActiveUserIdList(chat.getMemberEvents());
        List<ChatMemberDTO> chatMemberDTOList = memberIdList.stream()
                .map(memberId -> new ChatMemberDTO(chat.getId(), memberId))
                .toList();
        EventDTO eventDTO = new EventDTO(userIdList, EventType.CHAT_MEMBER_ADD, chatMemberDTOList, userId);
        eventServiceClient.addEvent(eventDTO);
    }

    public void sendMemberDeleteEvent(Chat chat, List<UUID> memberIdList, UUID userId) {
        List<UUID> userIdList = ChatUtils.getActiveUserIdList(chat.getMemberEvents());
        List<ChatMemberDTO> chatMemberDTOList = memberIdList.stream()
                .map(memberId -> new ChatMemberDTO(chat.getId(), memberId))
                .toList();
        EventDTO eventDTO = new EventDTO(userIdList, EventType.CHAT_MEMBER_DELETE, chatMemberDTOList, userId);
        eventServiceClient.addEvent(eventDTO);
    }

    public void sendMemberLeaveEvent(Chat chat, UUID userId) {
        List<UUID> userIdList = ChatUtils.getActiveUserIdList(chat.getMemberEvents());
        ChatMemberDTO chatMemberDTO = new ChatMemberDTO(chat.getId(), userId);
        EventDTO eventDTO = new EventDTO(userIdList, EventType.CHAT_MEMBER_LEAVE, chatMemberDTO, userId);
        eventServiceClient.addEvent(eventDTO);
    }

    public void sendMessageReactionIncomingEvent(Reaction reaction) {
        List<UUID> userIdList = List.of(reaction.getMessage().getUserId());
        ReactionDTO reactionDTO = reactionMapper.pojoToDTO(reaction, reaction.getMessage().getChat().getId());
        EventDTO eventDTO = new EventDTO(userIdList, EventType.CHAT_REACTION_INCOMING,
                reactionDTO, reaction.getUserId());
        eventServiceClient.addEvent(eventDTO);
    }


}
