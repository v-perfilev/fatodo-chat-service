package com.persoff68.fatodo.builder;

import com.persoff68.fatodo.model.dto.ChatDTO;
import com.persoff68.fatodo.model.dto.WsChatEventDTO;
import lombok.Builder;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class TestWsChatEventDTO extends WsChatEventDTO {

    @Builder
    TestWsChatEventDTO(List<UUID> userIds, ChatDTO chatDTO) {
        super(userIds, chatDTO);
    }

    public static TestWsChatEventDTOBuilder defaultBuilder() {
        return TestWsChatEventDTO.builder()
                .userIds(Collections.singletonList(UUID.randomUUID()))
                .chatDTO(new ChatDTO());
    }

    public WsChatEventDTO toParent() {
        return new WsChatEventDTO(getUserIds(), getChat());
    }

}
