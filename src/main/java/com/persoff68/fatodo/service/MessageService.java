package com.persoff68.fatodo.service;

import com.persoff68.fatodo.model.Message;
import com.persoff68.fatodo.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;

    public void send(Message message, UUID senderId) {

    }

    public void edit(Message message, UUID senderId) {

    }

    public void delete(UUID messageId, UUID senderId) {

    }
}
