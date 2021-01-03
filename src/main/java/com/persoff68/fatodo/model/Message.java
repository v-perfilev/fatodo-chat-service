package com.persoff68.fatodo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
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
public class Message extends AbstractAuditingModel {

    @NotNull
    @Column(name = "chat_Id")
    private UUID chatId;

    @NotNull
    private UUID userId;

    @NotNull
    private String text;

    private boolean isDeleted = false;

    @OneToMany(cascade = CascadeType.ALL)
    private List<Status> statuses;

    @ManyToOne
    @JoinColumn(name = "chat_id", insertable = false, updatable = false)
    @JsonIgnore
    private Chat chat;

    public Message(UUID chatId, UUID userId, String text) {
        super();
        this.chatId = chatId;
        this.userId = userId;
        this.text = text;
    }

}
