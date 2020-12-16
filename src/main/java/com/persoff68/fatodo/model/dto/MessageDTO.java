package com.persoff68.fatodo.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class MessageDTO extends AbstractAuditingDTO {

    private long order;
    private UUID senderId;
    private UUID recipientId;
    private String text;
    private boolean isDeleted;

}
