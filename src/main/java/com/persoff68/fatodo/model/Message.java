package com.persoff68.fatodo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@Entity
@Table(name = "ftd_message")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Message extends AbstractAuditingModel {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @NotNull
    private long order;

    @NotNull
    private UUID senderId;

    @NotNull
    private UUID recipientId;

    @NotNull
    private String text;

    private boolean isDeleted = false;

}
