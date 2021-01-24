package com.persoff68.fatodo.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
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
import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "ftd_chat_reaction")
@Data
@NoArgsConstructor
@IdClass(Reaction.ReactionId.class)
public class Reaction {

    @Id
    private UUID messageId;

    @Id
    private UUID userId;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Type type;

    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    private Date timestamp = new Date();

    public Reaction(UUID messageId, UUID userId) {
        this.messageId = messageId;
        this.userId = userId;
    }

    @Data
    @AllArgsConstructor
    public static class ReactionId implements Serializable {
        private UUID messageId;
        private UUID userId;
    }

    public enum Type {
        LIKE,
        DISLIKE
    }

}
