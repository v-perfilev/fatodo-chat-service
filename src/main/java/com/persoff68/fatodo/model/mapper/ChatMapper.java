package com.persoff68.fatodo.model.mapper;

import com.persoff68.fatodo.model.Chat;
import com.persoff68.fatodo.model.dto.ChatDTO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ChatMapper {

    ChatDTO pojoToDTO(Chat chat);

}
