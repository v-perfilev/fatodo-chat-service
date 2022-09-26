package com.persoff68.fatodo.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class ChatDTO extends AbstractAuditingDTO {

    private String title;

    @JsonProperty("isDirect")
    private boolean isDirect;

    private List<ChatMemberDTO> members;

    MessageDTO lastMessage;

}
