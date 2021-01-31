package com.persoff68.fatodo.service;

import com.persoff68.fatodo.model.Chat;
import com.persoff68.fatodo.model.Message;
import com.persoff68.fatodo.repository.ChatRepository;
import com.persoff68.fatodo.repository.MessageRepository;
import com.persoff68.fatodo.service.exception.ModelNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class SystemMessageService {

    private final MessageRepository messageRepository;
    private final ChatRepository chatRepository;
    private final EntityManager entityManager;

    public void createStubMessage(UUID chatId) {
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(ModelNotFoundException::new);
        Message message = new Message(chat, null, false, true);
        messageRepository.save(message);
        entityManager.refresh(chat);
    }

}
