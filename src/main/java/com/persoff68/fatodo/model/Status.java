package com.persoff68.fatodo.model;

import com.persoff68.fatodo.config.constant.AppConstants;
import com.persoff68.fatodo.model.constant.StatusType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

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
public class Status implements Serializable {
    @Serial
    private static final long serialVersionUID = AppConstants.SERIAL_VERSION_UID;

    @Id
    @ManyToOne
    private Message message;

    @Id
    private UUID userId;

    @NotNull
    @Enumerated(EnumType.STRING)
    private StatusType type;

    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    private Date date = new Date();

    public static Status of(Message message, UUID userId, StatusType type) {
        Status status = new Status();
        status.message = message;
        status.userId = userId;
        status.type = type;
        return status;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StatusId implements Serializable {
        @Serial
        private static final long serialVersionUID = AppConstants.SERIAL_VERSION_UID;

        private Message message;
        private UUID userId;
    }

}
