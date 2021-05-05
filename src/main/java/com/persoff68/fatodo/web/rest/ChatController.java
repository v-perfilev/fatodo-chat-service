package com.persoff68.fatodo.web.rest;

import com.google.common.collect.Multimap;
import com.persoff68.fatodo.model.Chat;
import com.persoff68.fatodo.model.Message;
import com.persoff68.fatodo.model.dto.ChatDTO;
import com.persoff68.fatodo.model.mapper.ChatMapper;
import com.persoff68.fatodo.repository.OffsetPageRequest;
import com.persoff68.fatodo.security.exception.UnauthorizedException;
import com.persoff68.fatodo.security.util.SecurityUtils;
import com.persoff68.fatodo.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping(ChatController.ENDPOINT)
@RequiredArgsConstructor
@Transactional
public class ChatController {
    static final String ENDPOINT = "/api/chats";

    public static final int DEFAULT_SIZE = 20;

    private final ChatService chatService;
    private final ChatMapper chatMapper;

    @GetMapping
    public ResponseEntity<List<ChatDTO>> getAllPageable(@RequestParam(required = false) Integer offset,
                                                        @RequestParam(required = false) Integer size) {
        offset = Optional.ofNullable(offset).orElse(0);
        size = Optional.ofNullable(size).orElse(DEFAULT_SIZE);
        UUID userId = SecurityUtils.getCurrentId().orElseThrow(UnauthorizedException::new);
        Pageable pageRequest = OffsetPageRequest.of(offset, size);
        Map<Chat, Message> chatMap = chatService.getAllByUserId(userId, pageRequest);
        List<ChatDTO> chatDtoList = chatMap.entrySet().stream()
                .map(entry -> chatMapper.pojoToDTO(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(chatDtoList);
    }

    @GetMapping("/{chatId}")
    public ResponseEntity<ChatDTO> getById(@PathVariable UUID chatId) {
        UUID userId = SecurityUtils.getCurrentId().orElseThrow(UnauthorizedException::new);
        Chat chat = chatService.getByUserIdAndId(userId, chatId);
        ChatDTO chatDTO = chatMapper.pojoToDTO(chat);
        return ResponseEntity.ok(chatDTO);
    }

    @GetMapping(value = "/create-direct/{secondUserId}")
    public ResponseEntity<ChatDTO> createDirect(@PathVariable UUID secondUserId) {
        UUID firstUserId = SecurityUtils.getCurrentId().orElseThrow(UnauthorizedException::new);
        Chat chat = chatService.createDirect(firstUserId, secondUserId);
        ChatDTO chatDTO = chatMapper.pojoToDTO(chat);
        return ResponseEntity.status(HttpStatus.CREATED).body(chatDTO);
    }

    @PostMapping("/create-indirect")
    public ResponseEntity<ChatDTO> createIndirect(@RequestBody List<UUID> userIdList) {
        UUID firstUserId = SecurityUtils.getCurrentId().orElseThrow(UnauthorizedException::new);
        Chat chat = chatService.createIndirect(firstUserId, userIdList);
        ChatDTO chatDTO = chatMapper.pojoToDTO(chat);
        return ResponseEntity.status(HttpStatus.CREATED).body(chatDTO);
    }

    @PostMapping("/rename/{chatId}")
    public ResponseEntity<ChatDTO> rename(@PathVariable UUID chatId, @RequestBody String title) {
        UUID userId = SecurityUtils.getCurrentId().orElseThrow(UnauthorizedException::new);
        Chat chat = chatService.rename(userId, chatId, title);
        ChatDTO chatDTO = chatMapper.pojoToDTO(chat);
        return ResponseEntity.ok(chatDTO);
    }

    @GetMapping("/unread-messages-map")
    public ResponseEntity<Multimap<UUID, UUID>> getUnreadMessagesMap() {
        UUID userId = SecurityUtils.getCurrentId().orElseThrow(UnauthorizedException::new);
        Multimap<UUID, UUID> unreadMessageIdMultimap = chatService.getUnreadMessagesMap(userId);
        return ResponseEntity.ok(unreadMessageIdMultimap);
    }

}
