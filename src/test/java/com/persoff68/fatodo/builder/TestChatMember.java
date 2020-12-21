package com.persoff68.fatodo.builder;

import com.persoff68.fatodo.model.ChatMember;
import lombok.Builder;

import javax.validation.constraints.NotNull;
import java.util.UUID;

public class TestChatMember extends ChatMember {

    @Builder
    public TestChatMember(UUID id, @NotNull UUID chatId, @NotNull UUID userId, UUID lastReadMessageId) {
        super(chatId, userId, lastReadMessageId);
        super.id = id;
    }

    public static TestChatMemberBuilder defaultBuilder() {
        return TestChatMember.builder();
    }

    public ChatMember toParent() {
        ChatMember member = new ChatMember();
        member.setId(getId());
        member.setChatId(getChatId());
        member.setUserId(getUserId());
        member.setLastReadMessageId(getLastReadMessageId());
        return member;
    }

}
