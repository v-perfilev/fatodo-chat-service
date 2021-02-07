package com.persoff68.fatodo.model.mapper;

import com.persoff68.fatodo.model.Status;
import com.persoff68.fatodo.model.dto.StatusDTO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface StatusMapper {

    StatusDTO pojoToDTO(Status status);

}
