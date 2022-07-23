package com.persoff68.fatodo.web.rest;

import com.persoff68.fatodo.mapper.ChatMapper;
import com.persoff68.fatodo.mapper.MessageMapper;
import com.persoff68.fatodo.model.Chat;
import com.persoff68.fatodo.model.Message;
import com.persoff68.fatodo.model.dto.ChatInfoDTO;
import com.persoff68.fatodo.model.dto.MessageInfoDTO;
import com.persoff68.fatodo.security.exception.UnauthorizedException;
import com.persoff68.fatodo.security.util.SecurityUtils;
import com.persoff68.fatodo.service.ChatService;
import com.persoff68.fatodo.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(InfoController.ENDPOINT)
@RequiredArgsConstructor
@Transactional
public class InfoController {
    static final String ENDPOINT = "/api/info";

    private final ChatService chatService;
    private final MessageService messageService;
    private final ChatMapper chatMapper;
    private final MessageMapper messageMapper;

    @GetMapping(value = "/chat")
    public ResponseEntity<List<ChatInfoDTO>> getAllChatInfoByIds(@RequestParam("ids") List<UUID> chatIdList) {
        UUID userId = SecurityUtils.getCurrentId().orElseThrow(UnauthorizedException::new);
        List<Chat> chatList = chatService.getAllAllowedByIds(userId, chatIdList);
        List<ChatInfoDTO> dtoList = chatList.stream()
                .map(chatMapper::pojoToInfoDTO)
                .toList();
        return ResponseEntity.ok(dtoList);
    }

    @GetMapping(value = "/message")
    public ResponseEntity<List<MessageInfoDTO>> getAllMessageInfoByIds(@RequestParam("ids") List<UUID> messageIdList) {
        UUID userId = SecurityUtils.getCurrentId().orElseThrow(UnauthorizedException::new);
        List<Message> messageList = messageService.getAllAllowedByIds(userId, messageIdList);
        List<MessageInfoDTO> dtoList = messageList.stream()
                .map(messageMapper::pojoToInfoDTO)
                .toList();
        return ResponseEntity.ok(dtoList);
    }

}
