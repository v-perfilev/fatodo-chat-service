package com.persoff68.fatodo.service;

import com.persoff68.fatodo.model.Message;
import com.persoff68.fatodo.model.Status;
import com.persoff68.fatodo.model.constant.StatusType;
import com.persoff68.fatodo.repository.MessageRepository;
import com.persoff68.fatodo.service.client.WsService;
import com.persoff68.fatodo.service.exception.ModelNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class StatusService {

    private final MessageRepository messageRepository;
    private final ChatPermissionService chatPermissionService;
    private final WsService wsService;

    public void markAsRead(UUID userId, UUID messageId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(ModelNotFoundException::new);
        chatPermissionService.hasReadMessagePermission(message, userId);

        Optional<Status> statusOptional = message.getStatuses().stream()
                .filter(status -> status.getUserId().equals(userId)).findFirst();

        if (statusOptional.isEmpty()) {
            Status status = Status.of(message, userId, StatusType.READ);
            message.getStatuses().add(status);
            messageRepository.save(message);

            // WS
            wsService.sendMessageStatusEvent(status, userId);
        }
    }

}
