package com.persoff68.fatodo.builder;

import com.persoff68.fatodo.model.Chat;
import com.persoff68.fatodo.model.MemberEvent;
import com.persoff68.fatodo.model.Message;
import lombok.Builder;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TestChat extends Chat {

    @Builder
    public TestChat(UUID id,
                    String title,
                    boolean isDirect,
                    List<MemberEvent> memberEvents,
                    List<Message> messages) {
        super(title, isDirect, memberEvents, messages);
        super.id = id;
    }

    public static TestChatBuilder defaultBuilder() {
        return TestChat.builder()
                .isDirect(false)
                .memberEvents(new ArrayList<>())
                .messages(new ArrayList<>());
    }

    public Chat toParent() {
        Chat chat = new Chat();
        chat.setId(getId());
        chat.setTitle(getTitle());
        chat.setDirect(isDirect());
        chat.setMemberEvents(getMemberEvents());
        chat.setMessages(getMessages());
        return chat;
    }

}
