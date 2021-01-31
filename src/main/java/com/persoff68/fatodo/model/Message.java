package com.persoff68.fatodo.model;

import com.persoff68.fatodo.config.constant.AppConstants;
import com.persoff68.fatodo.security.util.SecurityUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "ftd_chat_message")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(exclude = "chat")
public class Message extends AbstractAuditingModel {

    @NotNull
    @ManyToOne
    private Chat chat;

    @NotNull
    private UUID userId;

    private String text;

    @OneToOne
    private Message forwardedMessage;

    private boolean isEvent = false;
    private boolean isStub = false;

    @OneToMany(cascade = CascadeType.ALL)
    private List<Status> statuses;

    @OneToMany(cascade = CascadeType.ALL)
    private List<Reaction> reactions;

    public Message(Chat chat, UUID userId, String text, Message forwardedMessage) {
        this.chat = chat;
        this.userId = userId;
        this.text = text;
        this.forwardedMessage = forwardedMessage;
    }

    public Message(Chat chat, String text, boolean isEvent, boolean isStub) {
        this.chat = chat;
        this.userId = AppConstants.SYSTEM_ID;
        this.text = text;
        this.isEvent = isEvent;
        this.isStub = isStub;
    }

}
