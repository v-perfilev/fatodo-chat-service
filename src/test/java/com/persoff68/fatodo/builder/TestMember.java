package com.persoff68.fatodo.builder;

import com.persoff68.fatodo.model.Member;
import lombok.Builder;

import javax.validation.constraints.NotNull;
import java.util.UUID;

public class TestMember extends Member {

    @Builder
    public TestMember(UUID id, @NotNull UUID chatId, @NotNull UUID userId) {
        super(chatId, userId);
        super.id = id;
    }

    public static TestMemberBuilder defaultBuilder() {
        return TestMember.builder();
    }

    public Member toParent() {
        Member member = new Member();
        member.setId(getId());
        member.setChatId(getChatId());
        member.setUserId(getUserId());
        return member;
    }

}
