package com.persoff68.fatodo.model.mapper;

import com.persoff68.fatodo.model.Chat;
import com.persoff68.fatodo.model.Message;
import com.persoff68.fatodo.model.dto.ChatDTO;
import com.persoff68.fatodo.model.dto.MessageDTO;
import com.persoff68.fatodo.service.util.ChatUtils;
import lombok.RequiredArgsConstructor;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;
import java.util.UUID;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR)
@RequiredArgsConstructor
public abstract class ChatMapper {

    private final MessageMapper messageMapper;

    abstract ChatDTO defaultPojoToDTO(Chat chat);

    public ChatDTO pojoToDTO(Chat chat) {
        if (chat == null) {
            return null;
        }
        List<UUID> memberList = ChatUtils.getActiveUserIdList(chat);
        ChatDTO dto = defaultPojoToDTO(chat);
        dto.setMembers(memberList);
        return dto;
    }

    public ChatDTO pojoToDTO(Chat chat, Message lastMessage) {
        if (chat == null) {
            return null;
        }
        ChatDTO dto = pojoToDTO(chat);
        MessageDTO messageDTO = messageMapper.defaultPojoToDTO(lastMessage);
        dto.setLastMessage(messageDTO);
        return dto;
    }

}
