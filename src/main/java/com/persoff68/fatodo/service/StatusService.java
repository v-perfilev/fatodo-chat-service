package com.persoff68.fatodo.service;

import com.persoff68.fatodo.model.Chat;
import com.persoff68.fatodo.model.Message;
import com.persoff68.fatodo.model.Status;
import com.persoff68.fatodo.repository.MessageRepository;
import com.persoff68.fatodo.repository.StatusRepository;
import com.persoff68.fatodo.service.exception.ModelNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StatusService {

    private final StatusRepository statusRepository;
    private final MessageRepository messageRepository;
    private final PermissionService permissionService;

    public void markMessageAsRead(UUID userId, UUID messageId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(ModelNotFoundException::new);
        Chat chat = Optional.of(message.getChat())
                .orElseThrow(ModelNotFoundException::new);
        permissionService.hasReadMessagePermission(chat, userId);
        Status.StatusId id = new Status.StatusId(messageId, userId);
        boolean statusExists = statusRepository.existsById(id);
        if (!statusExists) {
            Status status = Status.of(messageId, userId, Status.Type.READ);
            statusRepository.save(status);
        }
    }

}
