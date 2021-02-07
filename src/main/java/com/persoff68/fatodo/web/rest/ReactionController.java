package com.persoff68.fatodo.web.rest;

import com.persoff68.fatodo.security.exception.UnauthorizedException;
import com.persoff68.fatodo.security.util.SecurityUtils;
import com.persoff68.fatodo.service.ReactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping(ReactionController.ENDPOINT)
@RequiredArgsConstructor
public class ReactionController {
    static final String ENDPOINT = "/api/reaction";

    private final ReactionService reactionService;

    @GetMapping("/{messageId}/like")
    public ResponseEntity<Void> setLike(@PathVariable UUID messageId) {
        UUID userId = SecurityUtils.getCurrentId().orElseThrow(UnauthorizedException::new);
        reactionService.setLike(userId, messageId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{messageId}/dislike")
    public ResponseEntity<Void> setDislike(@PathVariable UUID messageId) {
        UUID userId = SecurityUtils.getCurrentId().orElseThrow(UnauthorizedException::new);
        reactionService.setDislike(userId, messageId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{messageId}/none")
    public ResponseEntity<Void> setNone(@PathVariable UUID messageId) {
        UUID userId = SecurityUtils.getCurrentId().orElseThrow(UnauthorizedException::new);
        reactionService.remove(userId, messageId);
        return ResponseEntity.ok().build();
    }

}
