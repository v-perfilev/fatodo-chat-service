package com.persoff68.fatodo.web.rest;

import com.persoff68.fatodo.model.Message;
import com.persoff68.fatodo.model.OffsetPageRequest;
import com.persoff68.fatodo.model.dto.MessageDTO;
import com.persoff68.fatodo.model.mapper.MessageMapper;
import com.persoff68.fatodo.security.exception.UnauthorizedException;
import com.persoff68.fatodo.security.util.SecurityUtils;
import com.persoff68.fatodo.service.MessageService;
import com.persoff68.fatodo.web.rest.vm.MessageVM;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping(MessageController.ENDPOINT)
@RequiredArgsConstructor
public class MessageController {
    static final String ENDPOINT = "/api/message";

    private static final int DEFAULT_LIMIT = 50;

    private final MessageService messageService;
    private final MessageMapper messageMapper;

    @GetMapping("/{chatId}")
    public ResponseEntity<List<MessageDTO>> getAllByChatIdPageable(@PathVariable UUID chatId,
                                                           @RequestParam long offset,
                                                           @RequestParam int limit) {
        UUID userId = SecurityUtils.getCurrentId().orElseThrow(UnauthorizedException::new);
        Pageable pageRequest = OffsetPageRequest.of(offset, limit > 1 ? limit : DEFAULT_LIMIT);
        List<Message> messageList = messageService.getAllByUserIdAndChatId(userId, chatId, pageRequest);
        List<MessageDTO> chatDtoList = messageList.stream()
                .map(messageMapper::pojoToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(chatDtoList);
    }

    @PostMapping("/direct/{recipientId}")
    public ResponseEntity<MessageDTO> sendDirect(@PathVariable UUID recipientId, @RequestBody MessageVM messageVM) {
        UUID userId = SecurityUtils.getCurrentId().orElseThrow(UnauthorizedException::new);
        Message message = messageService
                .sendDirect(userId, recipientId, messageVM.getText(), messageVM.getForwardedMessageId());
        MessageDTO messageDTO = messageMapper.pojoToDTO(message);
        return ResponseEntity.status(HttpStatus.CREATED).body(messageDTO);
    }

    @PostMapping("/{chatId}")
    public ResponseEntity<MessageDTO> send(@PathVariable UUID chatId, @RequestBody MessageVM messageVM) {
        UUID userId = SecurityUtils.getCurrentId().orElseThrow(UnauthorizedException::new);
        Message message = messageService
                .send(userId, chatId, messageVM.getText(), messageVM.getForwardedMessageId());
        MessageDTO messageDTO = messageMapper.pojoToDTO(message);
        return ResponseEntity.status(HttpStatus.CREATED).body(messageDTO);
    }

    @PutMapping("/{messageId}")
    public ResponseEntity<MessageDTO> edit(@PathVariable UUID messageId, @RequestBody MessageVM messageVM) {
        UUID userId = SecurityUtils.getCurrentId().orElseThrow(UnauthorizedException::new);
        Message message = messageService
                .edit(userId, messageId, messageVM.getText(), messageVM.getForwardedMessageId());
        MessageDTO messageDTO = messageMapper.pojoToDTO(message);
        return ResponseEntity.ok(messageDTO);
    }

    @DeleteMapping("/{messageId}")
    public ResponseEntity<Void> delete(@PathVariable UUID messageId) {
        UUID userId = SecurityUtils.getCurrentId().orElseThrow(UnauthorizedException::new);
        messageService.delete(userId, messageId);
        return ResponseEntity.ok().build();
    }

}
