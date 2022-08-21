package com.persoff68.fatodo.mapper;

import com.persoff68.fatodo.model.Status;
import com.persoff68.fatodo.model.dto.StatusDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.UUID;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface StatusMapper {

    @Mapping(target = "chatId", source = "chatId")
    StatusDTO pojoToDTO(Status status, UUID chatId);

}
