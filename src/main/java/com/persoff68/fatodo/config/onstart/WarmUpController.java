package com.persoff68.fatodo.config.onstart;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(WarmUpController.ENDPOINT)
public class WarmUpController {
    public static final String ENDPOINT = "/warm-up";

    @GetMapping
    public ResponseEntity<Void> warmUp() {
        return ResponseEntity.ok().build();
    }

}
