package com.persoff68.fatodo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.Hibernate;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatContainer {

    private Chat chat;

    private Message lastMessage;

    private List<MemberEvent> memberEvents;

    public ChatContainer(Message message) {
        Hibernate.initialize(message.getChat().getMemberEvents());
        this.chat = message.getChat();
        this.lastMessage = message;
        this.memberEvents = message.getChat().getMemberEvents();
    }

}
