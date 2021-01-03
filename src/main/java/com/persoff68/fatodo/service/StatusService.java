package com.persoff68.fatodo.service;

import com.persoff68.fatodo.model.Chat;
import com.persoff68.fatodo.model.Message;
import com.persoff68.fatodo.model.Status;
import com.persoff68.fatodo.repository.MessageRepository;
import com.persoff68.fatodo.repository.StatusRepository;
import com.persoff68.fatodo.service.exception.ModelNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatusService {

    private final StatusRepository statusRepository;
    private final MessageRepository messageRepository;
    private final PermissionService permissionService;

    public List<UUID> getUserIdReadByMessageId(UUID messageId) {
        List<Status> statusList = statusRepository.findAllByMessageId(messageId);
        return statusList.stream()
                .map(Status::getUserId)
                .collect(Collectors.toList());
    }

    public void markMessageAsRead(UUID userId, UUID messageId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(ModelNotFoundException::new);
        Chat chat = Optional.of(message.getChat())
                .orElseThrow(ModelNotFoundException::new);
        permissionService.hasReadMessagePermission(chat, userId);
        Status status = new Status(messageId, userId);
        statusRepository.save(status);
    }

}
