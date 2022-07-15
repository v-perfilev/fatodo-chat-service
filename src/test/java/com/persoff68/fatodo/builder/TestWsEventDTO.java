package com.persoff68.fatodo.builder;

import com.persoff68.fatodo.model.dto.WsEventDTO;
import lombok.Builder;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class TestWsEventDTO<T> extends WsEventDTO<T> {

    @Builder
    public TestWsEventDTO(List<UUID> userIds, T content) {
        super(userIds, content);
    }

    public static <Z> TestWsEventDTOBuilder<Z> defaultBuilder() {
        return TestWsEventDTO.<Z>builder()
                .userIds(Collections.singletonList(UUID.randomUUID()));
    }

    public WsEventDTO<T> toParent() {
        return new WsEventDTO<>(getUserIds(), getContent());
    }

}
