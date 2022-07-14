package com.persoff68.fatodo.service;

import com.persoff68.fatodo.model.Message;
import com.persoff68.fatodo.model.Status;
import com.persoff68.fatodo.model.StatusId;
import com.persoff68.fatodo.model.constant.StatusType;
import com.persoff68.fatodo.repository.MessageRepository;
import com.persoff68.fatodo.repository.StatusRepository;
import com.persoff68.fatodo.service.exception.ModelNotFoundException;
import com.persoff68.fatodo.service.client.WsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class StatusService {

    private final StatusRepository statusRepository;
    private final MessageRepository messageRepository;
    private final ChatPermissionService chatPermissionService;
    private final EntityManager entityManager;
    private final WsService wsService;

    public void markAsRead(UUID userId, UUID messageId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(ModelNotFoundException::new);
        chatPermissionService.hasReadMessagePermission(message, userId);

        StatusId id = new StatusId(messageId, userId);
        boolean statusExists = statusRepository.existsById(id);
        if (!statusExists) {
            Status status = new Status(messageId, userId, StatusType.READ);
            statusRepository.saveAndFlush(status);
            entityManager.refresh(message);

            // WS
            wsService.sendMessageStatusEvent(message);
        }
    }

}
