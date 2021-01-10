package com.persoff68.fatodo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
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
@Table(name = "ftd_chat_status")
@Data
@NoArgsConstructor
@AllArgsConstructor
@IdClass(Status.StatusId.class)
public class Status {

    @Id
    private UUID messageId;

    @Id
    private UUID userId;

    @NotNull
    private Type type;

    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    private Date timestamp = new Date();

    @Data
    @AllArgsConstructor
    public static class StatusId implements Serializable {
        private UUID messageId;
        private UUID userId;
    }

    public enum Type {
        READ
    }

    private Status(UUID messageId, UUID userId, Type type) {
        this.messageId = messageId;
        this.userId = userId;
        this.type = type;
    }

    public static Status of (UUID messageId, UUID userId, Type type) {
        return new Status(messageId, userId, type);
    }

}
