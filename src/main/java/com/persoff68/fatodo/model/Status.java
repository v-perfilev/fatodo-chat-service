package com.persoff68.fatodo.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.persoff68.fatodo.config.constant.AppConstants;
import com.persoff68.fatodo.model.constant.StatusType;
import lombok.AllArgsConstructor;
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
import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "ftd_chat_status")
@Data
@NoArgsConstructor
@AllArgsConstructor
@IdClass(Status.StatusId.class)
@ToString(exclude = {"message"})
public class Status {

    @Id
    @Column(name = "message_id")
    private UUID messageId;

    @Id
    private UUID userId;

    @NotNull
    @Enumerated(EnumType.STRING)
    private StatusType type;

    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    private Date timestamp = new Date();

    @ManyToOne(targetEntity = Message.class)
    @JoinColumn(name = "message_id", insertable = false, updatable = false)
    @JsonBackReference
    private Message message;

    public Status(UUID messageId, UUID userId, StatusType type) {
        this.messageId = messageId;
        this.userId = userId;
        this.type = type;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StatusId implements Serializable {
        @Serial
        private static final long serialVersionUID = AppConstants.SERIAL_VERSION_UID;

        private UUID messageId;
        private UUID userId;
    }

}
