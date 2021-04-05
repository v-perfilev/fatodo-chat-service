package com.persoff68.fatodo.builder;

import com.persoff68.fatodo.model.constant.StatusType;
import com.persoff68.fatodo.model.dto.StatusDTO;
import lombok.Builder;

import java.util.Date;
import java.util.UUID;

public class TestStatusDTO extends StatusDTO {

    @Builder
    public TestStatusDTO(UUID messageId, UUID userId, StatusType type, Date timestamp) {
        super();
        super.setMessageId(messageId);
        super.setUserId(userId);
        super.setType(type);
        super.setTimestamp(timestamp);
    }

    public static TestStatusDTOBuilder defaultBuilder() {
        return TestStatusDTO.builder()
                .messageId(UUID.randomUUID())
                .userId(UUID.randomUUID())
                .type(StatusType.READ);
    }

    public StatusDTO toParent() {
        StatusDTO dto = new StatusDTO();
        dto.setUserId(getUserId());
        dto.setMessageId(getMessageId());
        dto.setType(getType());
        dto.setTimestamp(getTimestamp());
        return dto;
    }

}
