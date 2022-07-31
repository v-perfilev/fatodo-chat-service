package com.persoff68.fatodo.model.dto;


import com.persoff68.fatodo.model.constant.ClearEventType;
import lombok.Data;

import java.util.UUID;

@Data
public class ClearEventDTO {

    private ClearEventType type;

    private UUID id;

}
