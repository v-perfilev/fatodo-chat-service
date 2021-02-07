package com.persoff68.fatodo.web.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ChatController.ENDPOINT)
@RequiredArgsConstructor
public class ChatController {
    static final String ENDPOINT = "/api/chat";

}
