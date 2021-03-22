package com.persoff68.fatodo.model;

import com.persoff68.fatodo.model.constant.ReactionType;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.IdClass;
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
public class Reaction {

    @Id
    private UUID messageId;

    @Id
    private UUID userId;

    @NotNull
    @Enumerated(EnumType.STRING)
    private ReactionType type;

    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    private Date timestamp = new Date();

    public Reaction(UUID messageId, UUID userId, ReactionType type) {
        this.messageId = messageId;
        this.userId = userId;
        this.type = type;
    }

}
