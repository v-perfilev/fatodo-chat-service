package com.persoff68.fatodo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "ftd_chat_member_event")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class MemberEvent extends AbstractModel {

    @NotNull
    private UUID chatId;

    @NotNull
    private UUID userId;

    @NotNull
    private Type type;

    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    private Date timestamp = new Date();

    public enum Type {
        ADD_MEMBER,
        DELETE_MEMBER,
        CLEAR_HISTORY
    }

    public MemberEvent(UUID chatId, UUID userId, Type type) {
        this.chatId = chatId;
        this.userId = userId;
        this.type = type;
    }

}
