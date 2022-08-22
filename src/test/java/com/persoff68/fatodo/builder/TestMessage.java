package com.persoff68.fatodo.builder;

import com.persoff68.fatodo.model.Chat;
import com.persoff68.fatodo.model.Message;
import com.persoff68.fatodo.model.Reaction;
import com.persoff68.fatodo.model.Status;
import lombok.Builder;

import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class TestMessage extends Message {
    private static final String DEFAULT_VALUE = "test_value";

    @Builder
    public TestMessage(UUID id,
                       @NotNull Chat chat,
                       @NotNull UUID userId,
                       @NotNull String text,
                       Message reference,
                       boolean isEvent,
                       Set<Status> statuses,
                       Set<Reaction> reactions) {
        super();
        super.setChat(chat);
        super.setUserId(userId);
        super.setText(text);
        super.setReference(reference);
        super.setId(id);
        super.setEvent(isEvent);
        super.setStatuses(statuses);
        super.setReactions(reactions);
    }

    public static TestMessageBuilder defaultBuilder() {
        return TestMessage.builder()
                .text(DEFAULT_VALUE)
                .reactions(new HashSet<>())
                .statuses(new HashSet<>());
    }

    public Message toParent() {
        Message message = new Message();
        message.setId(getId());
        message.setChat(getChat());
        message.setUserId(getUserId());
        message.setText(getText());
        message.setReference(getReference());
        message.setEvent(isEvent());
        message.setStatuses(getStatuses());
        message.setReactions(getReactions());
        return message;
    }

}
