package com.persoff68.fatodo.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.persoff68.fatodo.config.constant.AppConstants;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.util.Set;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
public class MessageDTO extends AbstractAuditingDTO {
    @Serial
    private static final long serialVersionUID = AppConstants.SERIAL_VERSION_UID;

    private UUID chatId;
    private UUID userId;
    private String text;
    private MessageDTO reference;

    @JsonProperty("isDeleted")
    private boolean isDeleted;
    @JsonProperty("isEvent")
    private boolean isEvent;

    private Set<StatusDTO> statuses;
    private Set<ReactionDTO> reactions;

}
