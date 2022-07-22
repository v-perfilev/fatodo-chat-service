package com.persoff68.fatodo.model.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
public class ChatInfoDTO extends AbstractDTO {

    private String title;

    private List<UUID> members;

}
