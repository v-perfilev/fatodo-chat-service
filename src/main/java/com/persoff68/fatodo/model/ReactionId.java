package com.persoff68.fatodo.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.util.UUID;

@Data
@AllArgsConstructor
public class ReactionId implements Serializable {
    private UUID messageId;
    private UUID userId;
}
