package com.persoff68.fatodo.model.dto.event;

import com.persoff68.fatodo.model.constant.WsEventType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WsEventDTO {

    private List<UUID> userIds;

    private WsEventType type;

    private String payload;

    private UUID userId;

    private Date date;

    public WsEventDTO(List<UUID> userIds, WsEventType type, String payload, UUID userId) {
        this.userIds = userIds;
        this.type = type;
        this.payload = payload;
        this.userId = userId;
        this.date = new Date();
    }

}
