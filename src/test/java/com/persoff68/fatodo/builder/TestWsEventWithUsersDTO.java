package com.persoff68.fatodo.builder;

import com.persoff68.fatodo.model.constant.WsEventType;
import com.persoff68.fatodo.model.dto.WsEventWithUsersDTO;
import lombok.Builder;

import java.util.List;
import java.util.UUID;

public class TestWsEventWithUsersDTO extends WsEventWithUsersDTO {

    @Builder
    TestWsEventWithUsersDTO(List<UUID> userIdList, WsEventType type, Object payload) {
        super(userIdList, type, payload);
    }

    public static TestWsEventWithUsersDTOBuilder defaultBuilder() {
        return TestWsEventWithUsersDTO.builder()
                .userIdList(List.of(UUID.randomUUID()))
                .type(WsEventType.CHAT_CREATE);
    }

    public WsEventWithUsersDTO toParent() {
        WsEventWithUsersDTO dto = new WsEventWithUsersDTO();
        dto.setUserIds(getUserIds());
        dto.setType(getType());
        dto.setPayload(getPayload());
        return dto;
    }

}
