package com.persoff68.fatodo.mapper;

import com.persoff68.fatodo.model.Chat;
import com.persoff68.fatodo.model.ChatContainer;
import com.persoff68.fatodo.model.dto.ChatDTO;
import com.persoff68.fatodo.model.dto.ChatInfoDTO;
import com.persoff68.fatodo.model.dto.MessageDTO;
import com.persoff68.fatodo.service.util.ChatUtils;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.UUID;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public abstract class ChatMapper {

    @Autowired
    private MessageMapper messageMapper;

    abstract ChatDTO defaultPojoToDTO(Chat chat);

    abstract ChatInfoDTO defaultPojoToInfoDTO(Chat chat);

    public ChatDTO pojoToDTO(Chat chat) {
        if (chat == null) {
            return null;
        }
        List<UUID> memberList = ChatUtils.getActiveUserIdList(chat);
        ChatDTO dto = defaultPojoToDTO(chat);
        dto.setMembers(memberList);
        return dto;
    }

    public ChatDTO containerToDTO(ChatContainer chatContainer) {
        List<UUID> memberList = ChatUtils.getActiveUserIdList(chatContainer.getMemberEvents());
        MessageDTO messageDTO = messageMapper.pojoToDTO(chatContainer.getLastMessage());
        ChatDTO chatDTO = defaultPojoToDTO(chatContainer.getChat());
        chatDTO.setLastMessage(messageDTO);
        chatDTO.setMembers(memberList);
        return chatDTO;
    }

    public ChatInfoDTO pojoToInfoDTO(Chat chat) {
        if (chat == null) {
            return null;
        }
        List<UUID> memberList = ChatUtils.getActiveUserIdList(chat);
        ChatInfoDTO dto = defaultPojoToInfoDTO(chat);
        dto.setMembers(memberList);
        return dto;
    }

}
