package com.persoff68.fatodo.builder;

import com.persoff68.fatodo.model.dto.StatusDTO;
import com.persoff68.fatodo.model.dto.StatusesDTO;
import lombok.Builder;

import java.util.Collections;
import java.util.Set;
import java.util.UUID;

public class TestStatusesDTO extends StatusesDTO {

    @Builder
    TestStatusesDTO(UUID chatId, UUID messageId, Set<StatusDTO> statuses) {
        super();
        super.setChatId(chatId);
        super.setMessageId(messageId);
        super.setStatuses(statuses);
    }

    public static TestStatusesDTOBuilder defaultBuilder() {
        return TestStatusesDTO.builder()
                .chatId(UUID.randomUUID())
                .messageId(UUID.randomUUID())
                .statuses(Collections.singleton(TestStatusDTO.defaultBuilder().build().toParent()));
    }

    public StatusesDTO toParent() {
        StatusesDTO dto = new StatusesDTO();
        dto.setChatId(getChatId());
        dto.setMessageId(getMessageId());
        dto.setStatuses(getStatuses());
        return dto;
    }

}

