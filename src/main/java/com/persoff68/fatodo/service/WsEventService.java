package com.persoff68.fatodo.service;

import com.persoff68.fatodo.model.Chat;
import com.persoff68.fatodo.model.dto.ChatDTO;
import com.persoff68.fatodo.model.mapper.ChatMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WsEventService {
    private static final String CHAT_DESTINATION = "/user/chat/all";

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatMapper chatMapper;

    public void sendChatEvent(String user, Chat chat) {
        ChatDTO chatDTO = chatMapper.pojoToDTO(chat);
        messagingTemplate.convertAndSendToUser(user, CHAT_DESTINATION, chatDTO);
    }

}
