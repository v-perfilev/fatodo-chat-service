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
@Table(name = "ftd_chat_member")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ChatMember extends AbstractAuditingModel {

    @NotNull
    private UUID chatId;

    @NotNull
    private UUID userId;

    private UUID lastReadMessageId;

    public ChatMember(UUID chatId, UUID userId) {
        super();
        this.chatId = chatId;
        this.userId = userId;
    }

}
