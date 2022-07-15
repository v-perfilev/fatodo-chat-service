package com.persoff68.fatodo.model.dto;

import com.persoff68.fatodo.model.constant.StatusType;
import lombok.Data;

import java.util.Date;
import java.util.UUID;

@Data
public class StatusDTO {

    private UUID messageId;

    private UUID userId;

    private StatusType type;

    private Date timestamp;

}
