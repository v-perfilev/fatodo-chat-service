package com.persoff68.fatodo.model.mapper;

import com.persoff68.fatodo.model.ChatMessage;
import com.persoff68.fatodo.model.dto.MessageDTO;
import com.persoff68.fatodo.web.rest.vm.MessageVM;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MessageMapper {

    MessageDTO pojoToDTO(ChatMessage chatMessage);

    ChatMessage vmToPojo(MessageVM messageVM);

}
