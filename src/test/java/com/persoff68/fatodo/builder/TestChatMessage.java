package com.persoff68.fatodo.builder;

import com.persoff68.fatodo.model.ChatMember;
import com.persoff68.fatodo.model.ChatMessage;
import lombok.Builder;

import javax.validation.constraints.NotNull;
import java.util.UUID;

public class TestChatMessage extends ChatMessage {
    private static final String DEFAULT_VALUE = "test_value";

    @Builder
    public TestChatMessage(UUID id, @NotNull UUID chatId, @NotNull UUID userId, @NotNull String text, boolean isDeleted) {
        super(chatId, userId, text, isDeleted);
    }

    public static TestChatMessageBuilder defaultBuilder() {
        return TestChatMessage.builder()
                .chatId(UUID.randomUUID())
                .userId(UUID.randomUUID())
                .text(DEFAULT_VALUE)
                .isDeleted(false);
    }

    public ChatMessage toParent() {
        ChatMessage message = new ChatMessage();
        message.setId(getId());
        message.setChatId(getChatId());
        message.setUserId(getUserId());
        setText(getText());
        setDeleted(isDeleted());
        return message;
    }

}
