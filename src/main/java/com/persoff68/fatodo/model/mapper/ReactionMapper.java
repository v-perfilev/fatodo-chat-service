package com.persoff68.fatodo.model.mapper;

import com.persoff68.fatodo.model.Reaction;
import com.persoff68.fatodo.model.dto.ReactionDTO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ReactionMapper {

    ReactionDTO pojoToDTO(Reaction reaction);

}
