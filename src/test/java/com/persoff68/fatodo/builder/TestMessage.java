package com.persoff68.fatodo.builder;

import com.persoff68.fatodo.model.Chat;
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
                       @NotNull Chat chat,
                       @NotNull UUID userId,
                       @NotNull String text,
                       Message forwardedMessage,
                       boolean isEvent,
                       List<Status> statuses,
                       List<Reaction> reactions) {
        super();
        super.setChat(chat);
        super.setUserId(userId);
        super.setText(text);
        super.setForwardedMessage(forwardedMessage);
        super.setId(id);
        super.setEvent(isEvent);
        super.setStatuses(statuses);
        super.setReactions(reactions);
    }

    public static TestMessageBuilder defaultBuilder() {
        return TestMessage.builder().text(DEFAULT_VALUE);
    }

    public Message toParent() {
        Message message = new Message();
        message.setId(getId());
        message.setChat(getChat());
        message.setUserId(getUserId());
        message.setText(getText());
        message.setForwardedMessage(getForwardedMessage());
        message.setEvent(isEvent());
        message.setStatuses(getStatuses());
        message.setReactions(getReactions());
        return message;
    }

}
