package com.persoff68.fatodo.model;

import com.persoff68.fatodo.config.constant.AppConstants;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.io.Serial;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "ftd_chat_message")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true, exclude = {"chat", "reference", "statuses", "reactions"})
@ToString(exclude = {"chat", "reference", "statuses", "reactions"})
public class Message extends AbstractAuditingModel implements Serializable {
    @Serial
    private static final long serialVersionUID = AppConstants.SERIAL_VERSION_UID;

    @ManyToOne
    private Chat chat;

    @NotNull
    private UUID userId;

    private String text;

    @OneToOne
    private Message reference;

    private boolean isEvent = false;
    private boolean isPrivate = false;

    private boolean isDeleted = false;

    @OneToMany(cascade = {CascadeType.ALL}, mappedBy = "message", orphanRemoval = true, fetch = FetchType.EAGER)
    private Set<Status> statuses = new HashSet<>();

    @OneToMany(cascade = {CascadeType.ALL}, mappedBy = "message", orphanRemoval = true, fetch = FetchType.EAGER)
    private Set<Reaction> reactions = new HashSet<>();


    public static Message of(Chat chat, UUID userId, String text, Message reference) {
        Message message = new Message();
        message.chat = chat;
        message.userId = userId;
        message.text = text;
        message.reference = reference;
        return message;
    }

    public static Message event(Chat chat, UUID userId, String text) {
        Message message = new Message();
        message.chat = chat;
        message.userId = userId;
        message.text = text;
        message.isEvent = true;
        return message;
    }

    public static Message privateEvent(Chat chat, UUID userId, String text) {
        Message message = new Message();
        message.chat = chat;
        message.userId = userId;
        message.text = text;
        message.isEvent = true;
        message.isPrivate = true;
        return message;
    }

}
