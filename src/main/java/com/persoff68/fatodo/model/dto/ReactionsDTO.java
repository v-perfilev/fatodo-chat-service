package com.persoff68.fatodo.model.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Data
public class ReactionsDTO implements Serializable {

    private UUID chatId;
    private UUID messageId;

    private Set<ReactionDTO> reactions;

}
