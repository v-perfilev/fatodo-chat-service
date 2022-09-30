package com.persoff68.fatodo.mapper;

import com.persoff68.fatodo.model.Reaction;
import com.persoff68.fatodo.model.dto.ReactionDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ReactionMapper {

    @Mapping(target = "chatId", source = "message.chat.id")
    @Mapping(target = "messageId", source = "message.id")
    ReactionDTO pojoToDTO(Reaction reaction);

}
