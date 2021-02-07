package com.persoff68.fatodo.model.mapper;

import com.persoff68.fatodo.model.Message;
import com.persoff68.fatodo.model.dto.MessageDTO;
import com.persoff68.fatodo.model.dto.ReactionDTO;
import com.persoff68.fatodo.model.dto.StatusDTO;
import lombok.RequiredArgsConstructor;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR)
@RequiredArgsConstructor
public abstract class MessageMapper {

    private final ReactionMapper reactionMapper;
    private final StatusMapper statusMapper;

    abstract MessageDTO defaultPojoToDTO(Message message);

    public MessageDTO pojoToDTO(Message message) {
        if (message == null) {
            return null;
        }
        List<ReactionDTO> reactionDTOList = message.getReactions().stream()
                .map(reactionMapper::pojoToDTO)
                .collect(Collectors.toList());
        List<StatusDTO> statusDTOList = message.getStatuses().stream()
                .map(statusMapper::pojoToDTO)
                .collect(Collectors.toList());
        MessageDTO dto = defaultPojoToDTO(message);
        dto.setReactions(reactionDTOList);
        dto.setStatuses(statusDTOList);
        return dto;
    }

}
