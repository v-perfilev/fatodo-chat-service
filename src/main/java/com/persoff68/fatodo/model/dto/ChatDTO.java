package com.persoff68.fatodo.model.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
public class ChatDTO extends AbstractAuditingDTO {

    private String title;
    private boolean isDirect;

    private List<UUID> members;
    MessageDTO lastMessage;

}
