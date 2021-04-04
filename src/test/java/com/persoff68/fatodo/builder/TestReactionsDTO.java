package com.persoff68.fatodo.builder;

import com.persoff68.fatodo.model.dto.ReactionDTO;
import com.persoff68.fatodo.model.dto.ReactionsDTO;
import lombok.Builder;

import java.util.List;
import java.util.UUID;

public class TestReactionsDTO extends ReactionsDTO {

    @Builder
    TestReactionsDTO(UUID chatId, UUID messageId, List<ReactionDTO> reactions) {
        super();
        super.setChatId(chatId);
        super.setMessageId(messageId);
        super.setReactions(reactions);
    }

    public static TestReactionsDTOBuilder defaultBuilder() {
        return TestReactionsDTO.builder()
                .chatId(UUID.randomUUID())
                .messageId(UUID.randomUUID());
    }

    public ReactionsDTO toParent() {
        ReactionsDTO dto = new ReactionsDTO();
        dto.setChatId(getChatId());
        dto.setMessageId(getMessageId());
        dto.setReactions(getReactions());
        return dto;
    }

}
