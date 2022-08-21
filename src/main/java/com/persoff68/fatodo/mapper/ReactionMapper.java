package com.persoff68.fatodo.mapper;

import com.persoff68.fatodo.model.Reaction;
import com.persoff68.fatodo.model.dto.ReactionDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.UUID;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ReactionMapper {

    @Mapping(target = "chatId", source = "chatId")
    ReactionDTO pojoToDTO(Reaction reaction, UUID chatId);

}
