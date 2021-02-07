package com.persoff68.fatodo.model.mapper;

import com.persoff68.fatodo.model.Chat;
import com.persoff68.fatodo.model.Message;
import com.persoff68.fatodo.model.dto.ChatDTO;
import com.persoff68.fatodo.model.dto.MessageDTO;
import com.persoff68.fatodo.service.util.ChatUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ChatMessageMapper {

    private final ChatMapper chatMapper;
    private final MessageMapper messageMapper;

    public ChatDTO pojoToDTO(Chat chat, Message lastMessage) {
        if (chat == null) {
            return null;
        }
        ChatDTO dto = pojoToDTO(chat);
        MessageDTO messageDTO = messageMapper.pojoToDTO(lastMessage);
        dto.setLastMessage(messageDTO);
        return dto;
    }

    public ChatDTO pojoToDTO(Chat chat) {
        if (chat == null) {
            return null;
        }
        List<UUID> memberList = ChatUtils.getActiveUserIdList(chat);
        ChatDTO dto = chatMapper.pojoToDTO(chat);
        dto.setMembers(memberList);
        return dto;
    }

}
