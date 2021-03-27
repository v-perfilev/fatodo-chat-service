package com.persoff68.fatodo.model;

import com.google.common.collect.Lists;
import com.persoff68.fatodo.model.constant.EventMessageType;
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
import java.util.UUID;

@Entity
@Table(name = "ftd_chat_message")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(exclude = {"chat"})
public class Message extends AbstractAuditingModel {

    @ManyToOne
    private Chat chat;

    @NotNull
    private UUID userId;

    private String text;

    @OneToOne
    private Message forwardedMessage;

    private boolean isDeleted = false;
    private boolean isEvent = false;
    private boolean isStub = false;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "messageId", targetEntity = Status.class)
    private List<Status> statuses = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "messageId", targetEntity = Reaction.class)
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
        message.userId = userId;
        message.isStub = true;
        return message;
    }

    public static Message event(Chat chat, UUID userId, EventMessageType type, List<String> params) {
        Message message = new Message();
        message.chat = chat;
        message.userId = userId;
        message.text = buildEventText(type, params);
        message.isEvent = true;
        return message;
    }

    public static String buildEventText(EventMessageType type, List<String> params) {
        List<String> textList = Lists.newArrayList(type.toString());
        if (params != null) {
            textList.addAll(params);
        }
        return String.join(",", textList);
    }

}
