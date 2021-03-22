package com.persoff68.fatodo.service;

import com.persoff68.fatodo.model.Chat;
import com.persoff68.fatodo.model.Message;
import com.persoff68.fatodo.model.dto.ChatDTO;
import com.persoff68.fatodo.model.dto.MessageDTO;
import com.persoff68.fatodo.model.mapper.ChatMapper;
import com.persoff68.fatodo.model.mapper.MessageMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WsService {
    private static final String CHAT_NEW_DESTINATION = "/chat/new";
    private static final String CHAT_UPDATE_DESTINATION = "/chat/update";
    private static final String CHAT_DELETE_DESTINATION = "/chat/delete";
    private static final String CHAT_LAST_MESSAGE_DESTINATION = "/chat/last-message";
    private static final String MESSAGE_NEW_DESTINATION = "/message/new";
    private static final String MESSAGE_UPDATE_DESTINATION = "/message/update";
    private static final String MESSAGE_DELETE_DESTINATION = "/message/delete";

    private final UserService userService;
    private final SimpMessagingTemplate messagingTemplate;
    private final ChatMapper chatMapper;
    private final MessageMapper messageMapper;

    public void sendChatNewEvent(Chat chat) {
        sendChatEvent(chat, CHAT_NEW_DESTINATION);
    }

    public void sendChatUpdateEvent(Chat chat) {
        sendChatEvent(chat, CHAT_UPDATE_DESTINATION);
    }

    public void sendChatDeleteEvent(Chat chat) {
        sendChatEvent(chat, CHAT_DELETE_DESTINATION);
    }

    public void sendChatLastMessageEvent(Message message) {
        sendMessageEvent(message, CHAT_LAST_MESSAGE_DESTINATION);
    }

    public void sendMessageNewEvent(Message message) {
        sendMessageEvent(message, MESSAGE_NEW_DESTINATION);
    }

    public void sendMessageUpdateEvent(Message message) {
        sendMessageEvent(message, MESSAGE_UPDATE_DESTINATION);
    }

    public void sendMessageDeleteEvent(Message message) {
        sendMessageEvent(message, MESSAGE_DELETE_DESTINATION);
    }


    private void sendChatEvent(Chat chat, String destination) {
        List<String> usernameList = userService.getUsernamesFromChat(chat);
        ChatDTO chatDTO = chatMapper.pojoToDTO(chat);
        usernameList.forEach(username -> messagingTemplate.convertAndSendToUser(username, destination, chatDTO));
    }

    private void sendMessageEvent(Message message, String destination) {
        List<String> usernameList = userService.getUsernamesFromChat(message.getChat());
        MessageDTO messageDTO = messageMapper.pojoToDTO(message);
        usernameList.forEach(username -> messagingTemplate.convertAndSendToUser(username, destination, messageDTO));
    }

}
