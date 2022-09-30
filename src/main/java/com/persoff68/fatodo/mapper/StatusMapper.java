package com.persoff68.fatodo.mapper;

import com.persoff68.fatodo.model.Status;
import com.persoff68.fatodo.model.dto.StatusDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface StatusMapper {

    @Mapping(target = "chatId", source = "message.chat.id")
    @Mapping(target = "messageId", source = "message.id")
    StatusDTO pojoToDTO(Status status);

}
