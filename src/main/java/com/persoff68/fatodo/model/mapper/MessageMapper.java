package com.persoff68.fatodo.model.mapper;

import com.persoff68.fatodo.model.Chat;
import com.persoff68.fatodo.model.Message;
import com.persoff68.fatodo.model.dto.MessageDTO;
import com.persoff68.fatodo.model.dto.ReactionDTO;
import com.persoff68.fatodo.model.dto.ReactionsDTO;
import com.persoff68.fatodo.model.dto.StatusDTO;
import com.persoff68.fatodo.model.dto.StatusesDTO;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public abstract class MessageMapper {

    @Autowired
    private ReactionMapper reactionMapper;
    @Autowired
    private StatusMapper statusMapper;

    @Mapping(target = "forwardedMessage", ignore = true)
    @Mapping(target = "statuses", ignore = true)
    @Mapping(target = "reactions", ignore = true)
    abstract MessageDTO defaultPojoToDTO(Message message);

    public MessageDTO pojoToDTO(Message message) {
        if (message == null) {
            return null;
        }
        Chat chat = message.getChat();
        UUID chatId = chat != null ? chat.getId() : null;

        MessageDTO forwardedMessageDTO = pojoToDTO(message.getForwardedMessage());

        List<ReactionDTO> reactionDTOList = message.getReactions() != null
                ? message.getReactions().stream()
                .map(reactionMapper::pojoToDTO)
                .collect(Collectors.toList())
                : Collections.emptyList();

        List<StatusDTO> statusDTOList = message.getStatuses() != null
                ? message.getStatuses().stream()
                .map(statusMapper::pojoToDTO)
                .collect(Collectors.toList())
                : Collections.emptyList();

        MessageDTO dto = defaultPojoToDTO(message);
        dto.setChatId(chatId);
        dto.setForwardedMessage(forwardedMessageDTO);
        dto.setReactions(reactionDTOList);
        dto.setStatuses(statusDTOList);
        return dto;
    }

    public StatusesDTO pojoToStatusesDTO(Message message) {
        Chat chat = message.getChat();
        UUID chatId = chat != null ? chat.getId() : null;

        List<StatusDTO> statusDTOList = message.getStatuses() != null
                ? message.getStatuses().stream()
                .map(statusMapper::pojoToDTO)
                .collect(Collectors.toList())
                : Collections.emptyList();

        StatusesDTO dto = new StatusesDTO();
        dto.setChatId(chatId);
        dto.setMessageId(message.getId());
        dto.setStatuses(statusDTOList);
        return dto;
    }

    public ReactionsDTO pojoToReactionsDTO(Message message) {
        Chat chat = message.getChat();
        UUID chatId = chat != null ? chat.getId() : null;

        List<ReactionDTO> reactionDTOList = message.getReactions() != null
                ? message.getReactions().stream()
                .map(reactionMapper::pojoToDTO)
                .collect(Collectors.toList())
                : Collections.emptyList();

        ReactionsDTO dto = new ReactionsDTO();
        dto.setChatId(chatId);
        dto.setMessageId(message.getId());
        dto.setReactions(reactionDTOList);
        return dto;
    }

}
