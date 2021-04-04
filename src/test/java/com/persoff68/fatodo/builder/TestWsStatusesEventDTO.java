package com.persoff68.fatodo.builder;

import com.persoff68.fatodo.model.dto.StatusesDTO;
import com.persoff68.fatodo.model.dto.WsStatusesEventDTO;
import lombok.Builder;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class TestWsStatusesEventDTO extends WsStatusesEventDTO {

    @Builder
    TestWsStatusesEventDTO(List<UUID> userIds, StatusesDTO statusesDTO) {
        super(userIds, statusesDTO);
    }

    public static TestWsStatusesEventDTOBuilder defaultBuilder() {
        return TestWsStatusesEventDTO.builder()
                .userIds(Collections.singletonList(UUID.randomUUID()))
                .statusesDTO(TestStatusesDTO.defaultBuilder().build().toParent());
    }

    public WsStatusesEventDTO toParent() {
        return new WsStatusesEventDTO(getUserIds(), getStatuses());
    }

}
