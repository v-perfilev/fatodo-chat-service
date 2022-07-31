package com.persoff68.fatodo.builder;

import com.persoff68.fatodo.model.constant.ClearEventType;
import com.persoff68.fatodo.model.dto.ClearEventDTO;
import lombok.Builder;

import java.util.UUID;

public class TestClearEventDTO extends ClearEventDTO {

    @Builder
    TestClearEventDTO(ClearEventType type, UUID id) {
        super();
        super.setType(type);
        super.setId(id);
    }

    public static TestClearEventDTOBuilder defaultBuilder() {
        return TestClearEventDTO.builder()
                .type(ClearEventType.CHAT)
                .id(UUID.randomUUID());
    }

    public ClearEventDTO toParent() {
        ClearEventDTO dto = new ClearEventDTO();
        dto.setType(getType());
        dto.setId(getId());
        return dto;
    }

}

