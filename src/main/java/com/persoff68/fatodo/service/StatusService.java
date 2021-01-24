package com.persoff68.fatodo.service;

import com.persoff68.fatodo.model.Message;
import com.persoff68.fatodo.model.Status;
import com.persoff68.fatodo.repository.MessageRepository;
import com.persoff68.fatodo.repository.StatusRepository;
import com.persoff68.fatodo.service.exception.ModelNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StatusService {

    private final StatusRepository statusRepository;
    private final MessageRepository messageRepository;
    private final PermissionService permissionService;
    private final EntityManager entityManager;

    @Transactional
    public void markMessageAsRead(UUID userId, UUID messageId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(ModelNotFoundException::new);
        permissionService.hasReadMessagePermission(message.getChat(), userId);

        Status.StatusId id = new Status.StatusId(messageId, userId);
        boolean statusExists = statusRepository.existsById(id);
        if (!statusExists) {
            Status status = new Status(messageId, userId, Status.Type.READ);
            statusRepository.save(status);
            entityManager.refresh(message);
        }
    }

}
