package com.persoff68.fatodo.model.dto;

import com.persoff68.fatodo.model.constant.ReactionType;
import lombok.Data;

import java.util.Date;
import java.util.UUID;

@Data
public class ReactionDTO {

    private UUID messageId;

    private UUID userId;

    private ReactionType type;

    private Date timestamp;

}
