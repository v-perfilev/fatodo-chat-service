package com.persoff68.fatodo.model.dto;

import com.persoff68.fatodo.model.ReactionType;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

@Data
public class ReactionDTO implements Serializable {

    private UUID messageId;
    private UUID userId;
    private ReactionType type;
    private Date timestamp;

}
