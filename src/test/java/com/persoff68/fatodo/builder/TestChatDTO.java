package com.persoff68.fatodo.builder;

import com.persoff68.fatodo.model.dto.ChatDTO;
import com.persoff68.fatodo.model.dto.MessageDTO;
import lombok.Builder;

import java.util.List;
import java.util.UUID;

public class TestChatDTO extends ChatDTO {

    @Builder
    public TestChatDTO(UUID id,
                       String title,
                       boolean isDirect,
                       List<UUID> members,
                       MessageDTO lastMessage) {
        super();
        super.id = id;
        super.setTitle(title);
        super.setDirect(isDirect);
        super.setMembers(members);
        super.setLastMessage(lastMessage);
    }

    public static TestChatDTOBuilder defaultBuilder() {
        return TestChatDTO.builder()
                .id(UUID.randomUUID())
                .isDirect(false);
    }

    public ChatDTO toParent() {
        ChatDTO dto = new ChatDTO();
        dto.setId(getId());
        dto.setTitle(getTitle());
        dto.setDirect(isDirect());
        dto.setMembers(getMembers());
        dto.setLastMessage(getLastMessage());
        return dto;
    }

}
