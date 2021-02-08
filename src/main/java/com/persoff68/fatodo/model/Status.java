package com.persoff68.fatodo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "ftd_chat_status")
@Data
@NoArgsConstructor
@AllArgsConstructor
@IdClass(StatusId.class)
public class Status {

    @Id
    private UUID messageId;

    @Id
    private UUID userId;

    @NotNull
    @Enumerated(EnumType.STRING)
    private StatusType type;

    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    private Date timestamp = new Date();

    public Status(UUID messageId, UUID userId, StatusType type) {
        this.messageId = messageId;
        this.userId = userId;
        this.type = type;
    }

}
