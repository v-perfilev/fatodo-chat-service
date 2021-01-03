package com.persoff68.fatodo.builder;

import com.persoff68.fatodo.model.Message;
import lombok.Builder;

import javax.validation.constraints.NotNull;
import java.util.UUID;

public class TestMessage extends Message {
    private static final String DEFAULT_VALUE = "test_value";

    @Builder
    public TestMessage(UUID id, @NotNull UUID chatId, @NotNull UUID userId, @NotNull String text, boolean isDeleted) {
        super(chatId, userId, text, isDeleted);
    }

    public static TestChatMessageBuilder defaultBuilder() {
        return TestMessage.builder()
                .chatId(UUID.randomUUID())
                .userId(UUID.randomUUID())
                .text(DEFAULT_VALUE)
                .isDeleted(false);
    }

    public Message toParent() {
        Message message = new Message();
        message.setId(getId());
        message.setChatId(getChatId());
        message.setUserId(getUserId());
        setText(getText());
        setDeleted(isDeleted());
        return message;
    }

}
