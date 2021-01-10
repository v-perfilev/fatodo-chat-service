package com.persoff68.fatodo.builder;

import com.persoff68.fatodo.model.Message;
import com.persoff68.fatodo.model.Reaction;
import com.persoff68.fatodo.model.Status;
import lombok.Builder;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

public class TestMessage extends Message {
    private static final String DEFAULT_VALUE = "test_value";

    @Builder
    public TestMessage(UUID id,
                       @NotNull UUID chatId,
                       @NotNull UUID userId,
                       @NotNull String text,
                       UUID forwardedMessageId,
                       boolean isEvent,
                       List<Status> statuses,
                       List<Reaction> reactions) {
        super(chatId, userId, text, forwardedMessageId, isEvent);
        super.setId(id);
        super.setStatuses(statuses);
        super.setReactions(reactions);
    }

    public static TestMessageBuilder defaultBuilder() {
        return TestMessage.builder()
                .chatId(UUID.randomUUID())
                .userId(UUID.randomUUID())
                .text(DEFAULT_VALUE);
    }

    public Message toParent() {
        Message message = new Message();
        message.setId(getId());
        message.setChatId(getChatId());
        message.setUserId(getUserId());
        message.setText(getText());
        message.setForwardedMessageId(getForwardedMessageId());
        message.setEvent(isEvent());
        message.setStatuses(getStatuses());
        message.setReactions(getReactions());
        return message;
    }

}
