package com.persoff68.fatodo.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateChatEventDTO {

    private EventType type;

    private List<UUID> recipientIds;

    private UUID userId;

    private UUID chatId;

    private UUID messageId;

    private String reaction;

    private List<UUID> userIds;

    public enum EventType {
        CHAT_CREATE,
        CHAT_UPDATE,
        CHAT_MEMBER_ADD,
        CHAT_MEMBER_DELETE,
        CHAT_MEMBER_LEAVE,
        CHAT_REACTION,
    }

    public static CreateChatEventDTO chatCreate(List<UUID> recipientIds, UUID chatId, UUID userId,
                                                List<UUID> userIds) {
        CreateChatEventDTO dto = new CreateChatEventDTO();
        dto.setType(EventType.CHAT_CREATE);
        dto.setRecipientIds(recipientIds);
        dto.setChatId(chatId);
        dto.setUserId(userId);
        dto.setUserIds(userIds);
        return dto;
    }

    public static CreateChatEventDTO chatUpdate(List<UUID> recipientIds, UUID chatId, UUID userId) {
        CreateChatEventDTO dto = new CreateChatEventDTO();
        dto.setType(EventType.CHAT_UPDATE);
        dto.setRecipientIds(recipientIds);
        dto.setChatId(chatId);
        dto.setUserId(userId);
        return dto;
    }

    public static CreateChatEventDTO chatMemberAdd(List<UUID> recipientIds, UUID chatId, UUID userId,
                                                   List<UUID> userIds) {
        CreateChatEventDTO dto = new CreateChatEventDTO();
        dto.setType(EventType.CHAT_MEMBER_ADD);
        dto.setRecipientIds(recipientIds);
        dto.setChatId(chatId);
        dto.setUserId(userId);
        dto.setUserIds(userIds);
        return dto;
    }

    public static CreateChatEventDTO chatMemberDelete(List<UUID> recipientIds, UUID chatId, UUID userId,
                                                      List<UUID> userIds) {
        CreateChatEventDTO dto = new CreateChatEventDTO();
        dto.setType(EventType.CHAT_MEMBER_DELETE);
        dto.setRecipientIds(recipientIds);
        dto.setChatId(chatId);
        dto.setUserId(userId);
        dto.setUserIds(userIds);
        return dto;
    }

    public static CreateChatEventDTO chatMemberLeave(List<UUID> recipientIds, UUID chatId, UUID userId) {
        CreateChatEventDTO dto = new CreateChatEventDTO();
        dto.setType(EventType.CHAT_MEMBER_LEAVE);
        dto.setRecipientIds(recipientIds);
        dto.setChatId(chatId);
        dto.setUserId(userId);
        return dto;
    }

    public static CreateChatEventDTO chatReaction(UUID recipientId, UUID chatId, UUID userId, String reaction) {
        CreateChatEventDTO dto = new CreateChatEventDTO();
        dto.setType(EventType.CHAT_REACTION);
        dto.setRecipientIds(Collections.singletonList(recipientId));
        dto.setChatId(chatId);
        dto.setUserId(userId);
        dto.setReaction(reaction);
        return dto;
    }

}
