package com.persoff68.fatodo.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Set;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
public class MessageDTO extends AbstractAuditingDTO {

    private UUID chatId;

    private UUID userId;

    private String text;

    private MessageDTO reference;

    @JsonProperty("isDeleted")
    private boolean isDeleted;

    @JsonProperty("isEvent")
    private boolean isEvent;

    @JsonProperty("isLastMessage")
    private boolean isLastMessage;

    private Set<StatusDTO> statuses;

    private Set<ReactionDTO> reactions;

}
