package com.persoff68.fatodo.service.client;

import com.persoff68.fatodo.client.EventServiceClient;
import com.persoff68.fatodo.model.constant.ReactionType;
import com.persoff68.fatodo.model.dto.CreateChatEventDTO;
import com.persoff68.fatodo.model.dto.DeleteUserEventsDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventServiceClient eventServiceClient;

    public void sendChatCreateEvent(UUID chatId, UUID userId, List<UUID> userIdList) {
        List<UUID> recipientIdList = new ArrayList<>();
        recipientIdList.add(userId);
        recipientIdList.addAll(userIdList);
        CreateChatEventDTO dto = CreateChatEventDTO.chatCreate(recipientIdList, chatId, userId, userIdList);
        eventServiceClient.addChatEvent(dto);
    }

    public void sendChatUpdateEvent(List<UUID> recipientIdList, UUID chatId, UUID userId) {
        CreateChatEventDTO dto = CreateChatEventDTO.chatUpdate(recipientIdList, chatId, userId);
        eventServiceClient.addChatEvent(dto);
    }

    public void sendChatMemberAddEvent(List<UUID> recipientIdList, UUID chatId, UUID userId, List<UUID> userIdList) {
        CreateChatEventDTO dto = CreateChatEventDTO.chatMemberAdd(recipientIdList, chatId, userId, userIdList);
        eventServiceClient.addChatEvent(dto);
    }

    public void sendChatMemberDeleteEvent(List<UUID> recipientIdList, UUID chatId, UUID userId, List<UUID> userIdList) {
        CreateChatEventDTO dto = CreateChatEventDTO.chatMemberDelete(recipientIdList, chatId, userId, userIdList);
        eventServiceClient.addChatEvent(dto);
    }

    public void sendChatMemberLeaveEvent(List<UUID> recipientIdList, UUID chatId, UUID userId) {
        CreateChatEventDTO dto = CreateChatEventDTO.chatMemberLeave(recipientIdList, chatId, userId);
        eventServiceClient.addChatEvent(dto);
    }

    public void sendChatReactionEvent(UUID recipientId, UUID chatId, UUID userId, ReactionType reaction) {
        String reactionName = reaction != null ? reaction.name() : null;
        CreateChatEventDTO dto = CreateChatEventDTO.chatReaction(recipientId, chatId, userId, reactionName);
        eventServiceClient.addChatEvent(dto);
    }

    public void deleteChatEventsForUser(UUID chatId, List<UUID> userIdList) {
        DeleteUserEventsDTO dto = new DeleteUserEventsDTO(chatId, userIdList);
        eventServiceClient.deleteChatEventsForUser(dto);
    }

}
