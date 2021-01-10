package com.persoff68.fatodo.builder;

import com.persoff68.fatodo.model.MemberEvent;
import lombok.Builder;

import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.UUID;

public class TestMemberEvent extends MemberEvent {

    @Builder
    public TestMemberEvent(UUID id,
                           @NotNull UUID chatId,
                           @NotNull UUID userId,
                           @NotNull Type type,
                           @NotNull Date timestamp) {
        super(chatId, userId, type, timestamp);
        super.id = id;
    }

    public static TestMemberEventBuilder defaultBuilder() {
        return TestMemberEvent.builder();
    }

    public MemberEvent toParent() {
        MemberEvent member = new MemberEvent();
        member.setId(getId());
        member.setChatId(getChatId());
        member.setUserId(getUserId());
        member.setType(getType());
        member.setTimestamp(getTimestamp());
        return member;
    }

}
