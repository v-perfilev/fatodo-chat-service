package com.persoff68.fatodo.model.dto;

import com.persoff68.fatodo.model.constant.WsEventType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WsEventWithUsersDTO {

    private List<UUID> userIds;

    private WsEventType type;

    private Object payload;

}
