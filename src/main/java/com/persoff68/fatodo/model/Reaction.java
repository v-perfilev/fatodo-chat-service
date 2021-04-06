package com.persoff68.fatodo.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.persoff68.fatodo.model.constant.ReactionType;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "ftd_chat_reaction")
@Data
@NoArgsConstructor
@IdClass(ReactionId.class)
@ToString(exclude = {"message"})
public class Reaction {

    @Id
    @Column(name = "message_id")
    private UUID messageId;

    @Id
    private UUID userId;

    @NotNull
    @Enumerated(EnumType.STRING)
    private ReactionType type;

    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    private Date timestamp = new Date();

    @ManyToOne(targetEntity = Message.class)
    @JoinColumn(name = "message_id", insertable = false, updatable = false)
    @JsonBackReference
    private Message message;

    public Reaction(UUID messageId, UUID userId, ReactionType type) {
        this.messageId = messageId;
        this.userId = userId;
        this.type = type;
    }

}
