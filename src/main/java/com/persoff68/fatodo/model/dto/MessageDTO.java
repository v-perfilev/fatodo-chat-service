package com.persoff68.fatodo.model.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
public class MessageDTO extends AbstractAuditingDTO {
    private UUID id;

    private UUID chatId;
    private UUID userId;
    private String text;
    private MessageDTO forwardedMessage;

    private boolean isDeleted;
    private boolean isEvent;

    private List<StatusDTO> statuses;
    private List<ReactionDTO> reactions;

}
