package com.persoff68.fatodo.builder;

import com.persoff68.fatodo.model.Member;
import lombok.Builder;

import javax.validation.constraints.NotNull;
import java.util.UUID;

public class TestMember extends Member {

    @Builder
    public TestMember(UUID id, @NotNull UUID chatId, @NotNull UUID userId, UUID lastReadMessageId) {
        super(chatId, userId, lastReadMessageId);
        super.id = id;
    }

    public static TestChatMemberBuilder defaultBuilder() {
        return TestMember.builder();
    }

    public Member toParent() {
        Member member = new Member();
        member.setId(getId());
        member.setChatId(getChatId());
        member.setUserId(getUserId());
        member.setLastReadMessageId(getLastReadMessageId());
        return member;
    }

}
