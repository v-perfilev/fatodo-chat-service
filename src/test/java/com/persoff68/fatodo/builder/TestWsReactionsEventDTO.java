package com.persoff68.fatodo.builder;

import com.persoff68.fatodo.model.dto.ReactionsDTO;
import com.persoff68.fatodo.model.dto.WsReactionsEventDTO;
import lombok.Builder;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class TestWsReactionsEventDTO extends WsReactionsEventDTO {

    @Builder
    TestWsReactionsEventDTO(List<UUID> userIds, ReactionsDTO reactionsDTO) {
        super(userIds, reactionsDTO);
    }

    public static TestWsReactionsEventDTOBuilder defaultBuilder() {
        return TestWsReactionsEventDTO.builder()
                .userIds(Collections.singletonList(UUID.randomUUID()))
                .reactionsDTO(TestReactionsDTO.defaultBuilder().build().toParent());
    }

    public WsReactionsEventDTO toParent() {
        return new WsReactionsEventDTO(getUserIds(), getReactions());
    }

}
