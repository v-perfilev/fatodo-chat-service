package com.persoff68.fatodo.web.rest;

import com.persoff68.fatodo.model.Message;
import com.persoff68.fatodo.model.mapper.MessageMapper;
import com.persoff68.fatodo.security.exception.UnauthorizedException;
import com.persoff68.fatodo.security.util.SecurityUtils;
import com.persoff68.fatodo.service.MessageService;
import com.persoff68.fatodo.web.rest.vm.MessageVM;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.UUID;

@RestController
@RequestMapping(MessageController.ENDPOINT)
@RequiredArgsConstructor
public class MessageController {
    static final String ENDPOINT = "/api/messages";

    private final MessageService messageService;
    private final MessageMapper messageMapper;

    @GetMapping(value = "/{userId}")
    public ResponseEntity<Void> getMessagesByUserId(@PathVariable UUID userId) {
        UUID id = SecurityUtils.getCurrentId().orElseThrow(UnauthorizedException::new);

        return ResponseEntity.ok().build();
    }

    @PostMapping
    public ResponseEntity<Void> sendMessage(@RequestBody @Valid MessageVM messageVM) {
        UUID id = SecurityUtils.getCurrentId().orElseThrow(UnauthorizedException::new);
        Message message = messageMapper.vmToPojo(messageVM);
        messageService.send(message, id);
        return ResponseEntity.ok().build();
    }

    @PutMapping
    public ResponseEntity<Void> editMessage(@RequestBody @Valid MessageVM messageVM) {
        UUID id = SecurityUtils.getCurrentId().orElseThrow(UnauthorizedException::new);
        Message message = messageMapper.vmToPojo(messageVM);
        messageService.edit(message, id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping(value = "/{messageId}")
    public ResponseEntity<Void> deleteMessage(@PathVariable UUID messageId) {
        UUID id = SecurityUtils.getCurrentId().orElseThrow(UnauthorizedException::new);
        messageService.delete(messageId, id);
        return ResponseEntity.ok().build();
    }

}
