package com.persoff68.fatodo.builder;

import com.persoff68.fatodo.model.Chat;
import com.persoff68.fatodo.model.MemberEvent;
import com.persoff68.fatodo.model.constant.MemberEventType;
import lombok.Builder;

import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.UUID;

public class TestMemberEvent extends MemberEvent {

    @Builder
    public TestMemberEvent(UUID id,
                           @NotNull Chat chat,
                           @NotNull UUID userId,
                           @NotNull MemberEventType type,
                           @NotNull Date date) {
        super(chat, userId, type, date);
        super.id = id;
    }

    public static TestMemberEventBuilder defaultBuilder() {
        return TestMemberEvent.builder()
                .type(MemberEventType.ADD_MEMBER)
                .date(new Date());
    }

    public MemberEvent toParent() {
        MemberEvent member = new MemberEvent();
        member.setId(getId());
        member.setChat(getChat());
        member.setUserId(getUserId());
        member.setType(getType());
        member.setDate(getDate());
        return member;
    }

}
