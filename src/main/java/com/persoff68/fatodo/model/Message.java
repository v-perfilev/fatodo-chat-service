package com.persoff68.fatodo.model;

import com.persoff68.fatodo.config.constant.AppConstants;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
    private List<Status> statuses = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL)
    private List<Reaction> reactions = new ArrayList<>();

    public static Message of(Chat chat, UUID userId, String text, Message forwardedMessage) {
        Message message = new Message();
        message.chat = chat;
        message.userId = userId;
        message.text = text;
        message.forwardedMessage = forwardedMessage;
        return message;
    }

    public static Message stub(Chat chat, UUID userId) {
        Message message = new Message();
        message.chat = chat;
        message.userId = Optional.ofNullable(userId).orElse(AppConstants.SYSTEM_ID);
        message.isStub = true;
        return message;
    }

    public static Message event(Chat chat, UUID userId, String text) {
        Message message = new Message();
        message.chat = chat;
        message.userId = Optional.ofNullable(userId).orElse(AppConstants.SYSTEM_ID);
        message.text = text;
        message.isEvent = true;
        return message;
    }

}
