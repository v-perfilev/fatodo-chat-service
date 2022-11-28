package com.persoff68.fatodo.web.rest;

import com.persoff68.fatodo.service.SystemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping(SystemController.ENDPOINT)
@RequiredArgsConstructor
public class SystemController {
    static final String ENDPOINT = "/api/system";

    private final SystemService systemService;

    @DeleteMapping(value = "/{userId}")
    public ResponseEntity<Void> deleteAccountPermanently(@PathVariable UUID userId) {
        systemService.deleteAccountPermanently(userId);
        return ResponseEntity.ok().build();
    }

}
