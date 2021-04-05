package com.persoff68.fatodo.builder;

import com.persoff68.fatodo.model.dto.MessageDTO;
import com.persoff68.fatodo.model.dto.WsMessageEventDTO;
import lombok.Builder;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class TestWsMessageEventDTO extends WsMessageEventDTO {

    @Builder
    TestWsMessageEventDTO(List<UUID> userIds, MessageDTO messageDTO) {
        super(userIds, messageDTO);
    }

    public static TestWsMessageEventDTOBuilder defaultBuilder() {
        return TestWsMessageEventDTO.builder()
                .userIds(Collections.singletonList(UUID.randomUUID()))
                .messageDTO(TestMessageDTO.defaultBuilder().build().toParent());
    }

    public WsMessageEventDTO toParent() {
        return new WsMessageEventDTO(getUserIds(), getMessage());
    }

}
