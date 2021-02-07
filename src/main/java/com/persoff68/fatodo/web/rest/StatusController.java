package com.persoff68.fatodo.web.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(StatusController.ENDPOINT)
@RequiredArgsConstructor
public class StatusController {
    static final String ENDPOINT = "/api/status";

}
