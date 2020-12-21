package com.persoff68.fatodo.builder;

import com.persoff68.fatodo.model.Chat;
import com.persoff68.fatodo.model.ChatMember;
import com.persoff68.fatodo.model.ChatMessage;
import lombok.Builder;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TestChat extends Chat {
    private static final String DEFAULT_VALUE = "test_value";

    @Builder
    public TestChat(UUID id, String title, boolean isDirect, List<ChatMember> members, List<ChatMessage> messages) {
        super(title, isDirect, members, messages);
        super.id = id;
    }

    public static TestChatBuilder defaultBuilder() {
        return TestChat.builder()
                .title(DEFAULT_VALUE)
                .isDirect(true)
                .members(new ArrayList<>())
                .messages(new ArrayList<>());
    }

    public Chat toParent() {
        Chat chat = new Chat();
        chat.setId(getId());
        chat.setTitle(getTitle());
        chat.setDirect(isDirect());
        chat.setMembers(getMembers());
        chat.setMembers(getMembers());
        return chat;
    }

}
