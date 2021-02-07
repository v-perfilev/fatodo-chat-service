package com.persoff68.fatodo.model.dto;

import lombok.Data;

@Data
public class EventMessageDTO {

    MessageDTO message;
    ReactionDTO reaction;
    StatusDTO status;

}
