package com.persoff68.fatodo.web.rest;

import com.google.common.collect.Multimap;
import com.persoff68.fatodo.mapper.ChatMapper;
import com.persoff68.fatodo.model.Chat;
import com.persoff68.fatodo.model.ChatContainer;
import com.persoff68.fatodo.model.PageableList;
import com.persoff68.fatodo.model.dto.ChatDTO;
import com.persoff68.fatodo.model.vm.ChatRenameVM;
import com.persoff68.fatodo.repository.OffsetPageRequest;
import com.persoff68.fatodo.security.exception.UnauthorizedException;
import com.persoff68.fatodo.security.util.SecurityUtils;
import com.persoff68.fatodo.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping(ChatController.ENDPOINT)
@RequiredArgsConstructor
public class ChatController {
    static final String ENDPOINT = "/api/chat";

    public static final int DEFAULT_SIZE = 20;

    private final ChatService chatService;
    private final ChatMapper chatMapper;

    @GetMapping
    public ResponseEntity<PageableList<ChatDTO>> getAllPageable(@RequestParam(required = false) Integer offset,
                                                                @RequestParam(required = false) Integer size) {
        offset = Optional.ofNullable(offset).orElse(0);
        size = Optional.ofNullable(size).orElse(DEFAULT_SIZE);
        UUID userId = SecurityUtils.getCurrentId().orElseThrow(UnauthorizedException::new);
        Pageable pageRequest = OffsetPageRequest.of(offset, size);
        PageableList<ChatContainer> chatList = chatService.getAllByUserId(userId, pageRequest);
        PageableList<ChatDTO> dtoList = chatList.convert(chatMapper::containerToDTO);
        return ResponseEntity.ok(dtoList);
    }

    @GetMapping("/filter/{filter}")
    public ResponseEntity<List<ChatDTO>> getFiltered(@PathVariable @NotBlank String filter) {
        UUID userId = SecurityUtils.getCurrentId().orElseThrow(UnauthorizedException::new);
        List<ChatContainer> chatList = chatService.getFilteredByUserId(userId, filter);
        List<ChatDTO> chatDtoList = chatList.stream().map(chatMapper::containerToDTO).toList();
        return ResponseEntity.ok(chatDtoList);
    }

    @GetMapping("/{chatId}")
    public ResponseEntity<ChatDTO> getById(@PathVariable UUID chatId) {
        UUID userId = SecurityUtils.getCurrentId().orElseThrow(UnauthorizedException::new);
        Chat chat = chatService.getByUserIdAndId(userId, chatId);
        ChatDTO chatDTO = chatMapper.pojoToDTO(chat);
        return ResponseEntity.ok(chatDTO);
    }

    @PostMapping("/direct")
    public ResponseEntity<ChatDTO> createDirect(@RequestBody String secondUserIdString) {
        UUID firstUserId = SecurityUtils.getCurrentId().orElseThrow(UnauthorizedException::new);
        UUID secondUserId = UUID.fromString(secondUserIdString);
        ChatContainer chatContainer = chatService.createDirect(firstUserId, secondUserId);
        ChatDTO chatDTO = chatMapper.containerToDTO(chatContainer);
        return ResponseEntity.status(HttpStatus.CREATED).body(chatDTO);
    }

    @PostMapping("/indirect")
    public ResponseEntity<ChatDTO> createIndirect(@RequestBody List<UUID> userIdList) {
        UUID firstUserId = SecurityUtils.getCurrentId().orElseThrow(UnauthorizedException::new);
        ChatContainer chatContainer = chatService.createIndirect(firstUserId, userIdList);
        ChatDTO chatDTO = chatMapper.containerToDTO(chatContainer);
        return ResponseEntity.status(HttpStatus.CREATED).body(chatDTO);
    }

    @PutMapping("/{chatId}")
    public ResponseEntity<ChatDTO> rename(@PathVariable UUID chatId, @RequestBody @Valid ChatRenameVM vm) {
        UUID userId = SecurityUtils.getCurrentId().orElseThrow(UnauthorizedException::new);
        ChatContainer chatContainer = chatService.rename(userId, chatId, vm.getTitle());
        ChatDTO chatDTO = chatMapper.containerToDTO(chatContainer);
        return ResponseEntity.ok(chatDTO);
    }

    @GetMapping("/unread")
    public ResponseEntity<Multimap<UUID, UUID>> getUnreadMessagesMap() {
        UUID userId = SecurityUtils.getCurrentId().orElseThrow(UnauthorizedException::new);
        Multimap<UUID, UUID> unreadMessageIdMultimap = chatService.getUnreadMessagesMap(userId);
        return ResponseEntity.ok(unreadMessageIdMultimap);
    }

}
