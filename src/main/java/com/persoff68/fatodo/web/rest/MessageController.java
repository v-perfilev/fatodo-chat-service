package com.persoff68.fatodo.web.rest;

import com.persoff68.fatodo.mapper.MessageMapper;
import com.persoff68.fatodo.model.Message;
import com.persoff68.fatodo.model.PageableList;
import com.persoff68.fatodo.model.dto.MessageDTO;
import com.persoff68.fatodo.model.vm.MessageVM;
import com.persoff68.fatodo.repository.OffsetPageRequest;
import com.persoff68.fatodo.security.exception.UnauthorizedException;
import com.persoff68.fatodo.security.util.SecurityUtils;
import com.persoff68.fatodo.service.MessageService;
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

import javax.validation.Valid;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping(MessageController.ENDPOINT)
@RequiredArgsConstructor
public class MessageController {
    static final String ENDPOINT = "/api/message";

    private static final int DEFAULT_SIZE = 30;

    private final MessageService messageService;
    private final MessageMapper messageMapper;

    @GetMapping("/{chatId}")
    public ResponseEntity<PageableList<MessageDTO>> getAllByChatIdPageable(
            @PathVariable UUID chatId,
            @RequestParam(required = false) Integer offset,
            @RequestParam(required = false) Integer size) {
        offset = Optional.ofNullable(offset).orElse(0);
        size = Optional.ofNullable(size).orElse(DEFAULT_SIZE);
        UUID userId = SecurityUtils.getCurrentId().orElseThrow(UnauthorizedException::new);
        Pageable pageRequest = OffsetPageRequest.of(offset, size);
        PageableList<Message> messageList = messageService.getAllByUserIdAndChatId(userId, chatId, pageRequest);
        PageableList<MessageDTO> dtoList = messageList.convert(messageMapper::pojoToDTO);
        return ResponseEntity.ok(dtoList);
    }

    @PostMapping("/{recipientId}/direct")
    public ResponseEntity<MessageDTO> sendDirect(@PathVariable UUID recipientId,
                                                 @Valid @RequestBody MessageVM messageVM) {
        UUID userId = SecurityUtils.getCurrentId().orElseThrow(UnauthorizedException::new);
        Message message = messageService
                .sendDirect(userId, recipientId, messageVM.getText());
        MessageDTO messageDTO = messageMapper.pojoToDTO(message);
        return ResponseEntity.status(HttpStatus.CREATED).body(messageDTO);
    }

    @PostMapping("/{chatId}")
    public ResponseEntity<MessageDTO> send(@PathVariable UUID chatId,
                                           @Valid @RequestBody MessageVM messageVM) {
        UUID userId = SecurityUtils.getCurrentId().orElseThrow(UnauthorizedException::new);
        Message message = messageService
                .send(userId, chatId, messageVM.getText());
        MessageDTO messageDTO = messageMapper.pojoToDTO(message);
        return ResponseEntity.status(HttpStatus.CREATED).body(messageDTO);
    }

    @PutMapping("/{messageId}")
    public ResponseEntity<MessageDTO> edit(@PathVariable UUID messageId,
                                           @Valid @RequestBody MessageVM messageVM) {
        UUID userId = SecurityUtils.getCurrentId().orElseThrow(UnauthorizedException::new);
        Message message = messageService
                .edit(userId, messageId, messageVM.getText());
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
