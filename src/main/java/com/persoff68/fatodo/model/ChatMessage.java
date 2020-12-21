package com.persoff68.fatodo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@Entity
@Table(name = "ftd_chat_message")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ChatMessage extends AbstractAuditingModel {

    @NotNull
    private UUID chatId;

    @NotNull
    private UUID userId;

    @NotNull
    private String text;

    private boolean isDeleted = false;

    public ChatMessage(UUID chatId, UUID userId, String text) {
        super();
        this.chatId = chatId;
        this.userId = userId;
        this.text = text;
    }

}
