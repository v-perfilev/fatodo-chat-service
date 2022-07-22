package com.persoff68.fatodo.model.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class MessageInfoDTO extends AbstractDTO {

    private String text;

}
