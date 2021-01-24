package com.persoff68.fatodo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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
@ToString(exclude = "chat")
public class MemberEvent extends AbstractModel {

    @NotNull
    @ManyToOne
    private Chat chat;

    @NotNull
    private UUID userId;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Type type;

    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    private Date timestamp = new Date();

    public MemberEvent(Chat chat, UUID userId, Type type) {
        this.chat = chat;
        this.userId = userId;
        this.type = type;
    }

    public enum Type {
        ADD_MEMBER,
        DELETE_MEMBER,
        CLEAR_HISTORY
    }

}