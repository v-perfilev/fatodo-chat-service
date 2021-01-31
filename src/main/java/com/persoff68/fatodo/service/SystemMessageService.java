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
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class SystemMessageService {

    private final MessageRepository messageRepository;
    private final ChatRepository chatRepository;
    private final EntityManager entityManager;

    public void createStubMessages(UUID chatId, List<UUID> userIdList) {
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(ModelNotFoundException::new);

        List<Message> messageList = userIdList != null && !userIdList.isEmpty()
                ? userIdList.stream()
                .distinct()
                .map(id -> Message.stub(chat, id))
                .collect(Collectors.toList())
                : Collections.emptyList();

        messageRepository.saveAll(messageList);
        messageRepository.flush();
        entityManager.refresh(chat);
    }

}
