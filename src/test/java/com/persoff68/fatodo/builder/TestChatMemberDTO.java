package com.persoff68.fatodo.builder;

import com.persoff68.fatodo.model.dto.ChatMemberDTO;
import lombok.Builder;

import java.util.UUID;

public class TestChatMemberDTO extends ChatMemberDTO {

    @Builder
    public TestChatMemberDTO(UUID chatId, UUID userId) {
        super();
        super.setChatId(chatId);
        super.setUserId(userId);
    }

    public static TestChatMemberDTOBuilder defaultBuilder() {
        return TestChatMemberDTO.builder()
                .chatId(UUID.randomUUID())
                .userId(UUID.randomUUID());
    }

    public ChatMemberDTO toParent() {
        ChatMemberDTO dto = new ChatMemberDTO();
        dto.setChatId(getChatId());
        dto.setUserId(getUserId());
        return dto;
    }

}
