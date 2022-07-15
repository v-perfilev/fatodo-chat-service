package com.persoff68.fatodo.model.dto;

import lombok.Data;

import java.util.Set;
import java.util.UUID;

@Data
public class ReactionsDTO {

    private UUID chatId;
    private UUID messageId;

    private Set<ReactionDTO> reactions;

}
