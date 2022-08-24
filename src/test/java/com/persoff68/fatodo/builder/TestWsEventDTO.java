package com.persoff68.fatodo.builder;

import com.persoff68.fatodo.model.constant.WsEventType;
import com.persoff68.fatodo.model.dto.event.WsEventDTO;
import lombok.Builder;

import java.util.List;
import java.util.UUID;

public class TestWsEventDTO extends WsEventDTO {

    @Builder
    TestWsEventDTO(List<UUID> userIdList, WsEventType type, String payload, UUID userId) {
        super(userIdList, type, payload, userId);
    }

    public static TestWsEventDTOBuilder defaultBuilder() {
        return TestWsEventDTO.builder()
                .userIdList(List.of(UUID.randomUUID()))
                .type(WsEventType.CHAT_CREATE)
                .userId(UUID.randomUUID());
    }

    public WsEventDTO toParent() {
        WsEventDTO dto = new WsEventDTO();
        dto.setUserIds(getUserIds());
        dto.setType(getType());
        dto.setPayload(getPayload());
        dto.setUserId(getUserId());
        dto.setDate(getDate());
        return dto;
    }

}
