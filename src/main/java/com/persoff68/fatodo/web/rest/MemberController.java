package com.persoff68.fatodo.web.rest;

import com.persoff68.fatodo.security.exception.UnauthorizedException;
import com.persoff68.fatodo.security.util.SecurityUtils;
import com.persoff68.fatodo.service.MemberEventService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(MemberController.ENDPOINT)
@RequiredArgsConstructor
public class MemberController {
    static final String ENDPOINT = "/api/member";

    private final MemberEventService memberEventService;

    @PostMapping("/{chatId}")
    public ResponseEntity<Void> addUsers(@PathVariable UUID chatId, @RequestBody List<UUID> userIdList) {
        UUID userId = SecurityUtils.getCurrentId().orElseThrow(UnauthorizedException::new);
        memberEventService.addUsers(userId, chatId, userIdList);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{chatId}")
    public ResponseEntity<Void> removeUsers(@PathVariable UUID chatId, @RequestParam("ids") List<UUID> userIdList) {
        UUID userId = SecurityUtils.getCurrentId().orElseThrow(UnauthorizedException::new);
        memberEventService.removeUsers(userId, chatId, userIdList);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{chatId}/leave")
    public ResponseEntity<Void> leave(@PathVariable UUID chatId) {
        UUID userId = SecurityUtils.getCurrentId().orElseThrow(UnauthorizedException::new);
        memberEventService.leaveChat(userId, chatId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{chatId}/clear")
    public ResponseEntity<Void> clear(@PathVariable UUID chatId) {
        UUID userId = SecurityUtils.getCurrentId().orElseThrow(UnauthorizedException::new);
        memberEventService.clearChat(userId, chatId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{chatId}/delete")
    public ResponseEntity<Void> delete(@PathVariable UUID chatId) {
        UUID userId = SecurityUtils.getCurrentId().orElseThrow(UnauthorizedException::new);
        memberEventService.deleteChat(userId, chatId);
        return ResponseEntity.ok().build();
    }

}
