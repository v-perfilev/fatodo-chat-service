package com.persoff68.fatodo.web.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(MessageController.ENDPOINT)
@RequiredArgsConstructor
public class MessageController {
    static final String ENDPOINT = "/api/message";

}
