package com.persoff68.fatodo.builder;

import com.persoff68.fatodo.model.dto.MessageDTO;
import com.persoff68.fatodo.model.dto.ReactionDTO;
import com.persoff68.fatodo.model.dto.StatusDTO;
import lombok.Builder;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

public class TestMessageDTO extends MessageDTO {
    private static final String DEFAULT_VALUE = "test_value";

    @Builder
    public TestMessageDTO(UUID id,
                          @NotNull UUID chatId,
                          @NotNull UUID userId,
                          @NotNull String text,
                          MessageDTO forwardedMessage,
                          boolean isEvent,
                          List<StatusDTO> statuses,
                          List<ReactionDTO> reactions) {
        super();
        super.setId(id);
        super.setChatId(chatId);
        super.setUserId(userId);
        super.setText(text);
        super.setForwardedMessage(forwardedMessage);
        super.setEvent(isEvent);
        super.setStatuses(statuses);
        super.setReactions(reactions);
    }

    public static TestMessageDTOBuilder defaultBuilder() {
        return TestMessageDTO.builder()
                .id(UUID.randomUUID())
                .chatId(UUID.randomUUID())
                .userId(UUID.randomUUID())
                .text(DEFAULT_VALUE);
    }

    public MessageDTO toParent() {
        MessageDTO dto = new MessageDTO();
        dto.setId(getId());
        dto.setChatId(getChatId());
        dto.setUserId(getUserId());
        dto.setText(getText());
        dto.setForwardedMessage(getForwardedMessage());
        dto.setEvent(isEvent());
        dto.setStatuses(getStatuses());
        dto.setReactions(getReactions());
        return dto;
    }

}
