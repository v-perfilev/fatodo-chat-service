package com.persoff68.fatodo.service;

import com.persoff68.fatodo.client.UserServiceClient;
import com.persoff68.fatodo.model.Chat;
import com.persoff68.fatodo.model.Message;
import com.persoff68.fatodo.model.dto.ChatDTO;
import com.persoff68.fatodo.model.dto.MessageDTO;
import com.persoff68.fatodo.model.mapper.ChatMapper;
import com.persoff68.fatodo.model.mapper.MessageMapper;
import com.persoff68.fatodo.service.util.ChatUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WsService {
    private static final String CHAT_DESTINATION = "/chat/root";
    private static final String MESSAGE_DESTINATION = "/chat/message";

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatMapper chatMapper;
    private final MessageMapper messageMapper;
    private final UserServiceClient userServiceClient;

    public void sendChatEvent(Chat chat) {
        List<String> usernameList = getUsernamesFromChat(chat);

        ChatDTO chatDTO = chatMapper.pojoToDTO(chat);
        usernameList.forEach(username -> {
            messagingTemplate.convertAndSendToUser(username, CHAT_DESTINATION, chatDTO);
        });
    }

    public void sendMessageEvent(Chat chat, Message message) {
        List<String> usernameList = getUsernamesFromChat(chat);

        MessageDTO messageDTO = messageMapper.pojoToDTO(message);
        usernameList.forEach(username -> {
            messagingTemplate.convertAndSendToUser(username, MESSAGE_DESTINATION, messageDTO);
        });
    }

    private List<String> getUsernamesFromChat(Chat chat) {
        List<UUID> userIdList = ChatUtils.getActiveUserIdList(chat);
        return userServiceClient.getAllUsernamesByIds(userIdList);
    }

}
