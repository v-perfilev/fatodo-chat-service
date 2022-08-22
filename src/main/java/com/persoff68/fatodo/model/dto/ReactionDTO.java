package com.persoff68.fatodo.model.dto;

import com.persoff68.fatodo.model.constant.ReactionType;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.UUID;

@Data
@NoArgsConstructor
public class ReactionDTO {

    private UUID chatId;

    private UUID messageId;

    private UUID userId;

    private ReactionType type;

    private Date date;

}
