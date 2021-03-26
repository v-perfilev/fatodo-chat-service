package com.persoff68.fatodo.service;

import com.persoff68.fatodo.model.Chat;
import com.persoff68.fatodo.model.Message;
import com.persoff68.fatodo.model.constant.WsDestination;
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

    private final UserService userService;
    private final SimpMessagingTemplate messagingTemplate;
    private final ChatMapper chatMapper;
    private final MessageMapper messageMapper;

    public void sendChatNewEvent(Chat chat) {
        sendChatEvent(chat, WsDestination.CHAT_NEW.getValue());
    }

    public void sendChatUpdateEvent(Chat chat) {
        sendChatEvent(chat, WsDestination.CHAT_UPDATE.getValue());
    }

    public void sendChatDeleteEvent(Chat chat) {
        sendChatEvent(chat, WsDestination.CHAT_DELETE.getValue());
    }

    public void sendChatLastMessageEvent(Chat chat, Message message) {
        sendChatWithMessageEvent(chat, message, WsDestination.CHAT_LAST_MESSAGE.getValue());
    }

    public void sendMessageNewEvent(Message message) {
        sendMessageEvent(message, WsDestination.MESSAGE_NEW.getValue());
    }

    public void sendMessageUpdateEvent(Message message) {
        sendMessageEvent(message, WsDestination.MESSAGE_UPDATE.getValue());
    }


    private void sendChatEvent(Chat chat, String destination) {
        List<String> usernameList = userService.getUsernamesFromChat(chat);
        ChatDTO chatDTO = chatMapper.pojoToDTO(chat);
        usernameList.forEach(username -> messagingTemplate.convertAndSendToUser(username, destination, chatDTO));
    }

    private void sendChatWithMessageEvent(Chat chat, Message lastMessage, String destination) {
        List<String> usernameList = userService.getUsernamesFromChat(chat);
        ChatDTO chatDTO = chatMapper.pojoToDTO(chat, lastMessage);
        usernameList.forEach(username -> messagingTemplate.convertAndSendToUser(username, destination, chatDTO));
    }

    private void sendMessageEvent(Message message, String destination) {
        List<String> usernameList = userService.getUsernamesFromChat(message.getChat());
        MessageDTO messageDTO = messageMapper.pojoToDTO(message);
        usernameList.forEach(username -> messagingTemplate.convertAndSendToUser(username, destination, messageDTO));
    }

}
