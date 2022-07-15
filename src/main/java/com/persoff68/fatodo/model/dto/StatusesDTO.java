package com.persoff68.fatodo.model.dto;

import lombok.Data;

import java.util.Set;
import java.util.UUID;

@Data
public class StatusesDTO {

    private UUID chatId;

    private UUID messageId;

    private Set<StatusDTO> statuses;

}
