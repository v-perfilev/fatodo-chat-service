package com.persoff68.fatodo.web.rest;

import com.persoff68.fatodo.security.exception.UnauthorizedException;
import com.persoff68.fatodo.security.util.SecurityUtils;
import com.persoff68.fatodo.service.StatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping(StatusController.ENDPOINT)
@RequiredArgsConstructor
public class StatusController {
    static final String ENDPOINT = "/api/status";

    private final StatusService statusService;

    @PostMapping("/{messageId}/read")
    public ResponseEntity<Void> setMessageRead(@PathVariable UUID messageId) {
        UUID userId = SecurityUtils.getCurrentId().orElseThrow(UnauthorizedException::new);
        statusService.markMessageAsRead(userId, messageId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/{chatId}/chat/read")
    public ResponseEntity<Void> setChatRead(@PathVariable UUID chatId) {
        UUID userId = SecurityUtils.getCurrentId().orElseThrow(UnauthorizedException::new);
        statusService.markChatAsRead(userId, chatId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

}
