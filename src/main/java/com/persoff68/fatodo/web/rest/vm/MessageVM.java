package com.persoff68.fatodo.web.rest.vm;

import lombok.Data;

import java.util.UUID;

@Data
public class MessageVM {
    private String text;
    private UUID forwardedMessageId;
}
