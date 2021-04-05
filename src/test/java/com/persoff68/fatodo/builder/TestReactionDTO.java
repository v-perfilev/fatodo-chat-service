package com.persoff68.fatodo.builder;

import com.persoff68.fatodo.model.constant.ReactionType;
import com.persoff68.fatodo.model.dto.ReactionDTO;
import lombok.Builder;

import java.util.Date;
import java.util.UUID;

public class TestReactionDTO extends ReactionDTO {

    @Builder
    public TestReactionDTO(UUID messageId, UUID userId, ReactionType type, Date timestamp) {
        super();
        super.setMessageId(messageId);
        super.setUserId(userId);
        super.setType(type);
        super.setTimestamp(timestamp);
    }


    public static TestReactionDTOBuilder defaultBuilder() {
        return TestReactionDTO.builder()
                .messageId(UUID.randomUUID())
                .userId(UUID.randomUUID())
                .type(ReactionType.LIKE);
    }

    public ReactionDTO toParent() {
        ReactionDTO dto = new ReactionDTO();
        dto.setUserId(getUserId());
        dto.setMessageId(getMessageId());
        dto.setType(getType());
        dto.setTimestamp(getTimestamp());
        return dto;
    }

}
