package com.persoff68.fatodo.web.rest;

import com.persoff68.fatodo.security.exception.UnauthorizedException;
import com.persoff68.fatodo.security.util.SecurityUtils;
import com.persoff68.fatodo.service.ReactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping(ReactionController.ENDPOINT)
@RequiredArgsConstructor
public class ReactionController {
    static final String ENDPOINT = "/api/reaction";

    private final ReactionService reactionService;

    @PostMapping("/{messageId}/like")
    public ResponseEntity<Void> setLike(@PathVariable UUID messageId) {
        UUID userId = SecurityUtils.getCurrentId().orElseThrow(UnauthorizedException::new);
        reactionService.setLike(userId, messageId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/{messageId}/dislike")
    public ResponseEntity<Void> setDislike(@PathVariable UUID messageId) {
        UUID userId = SecurityUtils.getCurrentId().orElseThrow(UnauthorizedException::new);
        reactionService.setDislike(userId, messageId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/{messageId}")
    public ResponseEntity<Void> setNone(@PathVariable UUID messageId) {
        UUID userId = SecurityUtils.getCurrentId().orElseThrow(UnauthorizedException::new);
        reactionService.remove(userId, messageId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

}
