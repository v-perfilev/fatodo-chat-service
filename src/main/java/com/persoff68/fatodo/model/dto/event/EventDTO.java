package com.persoff68.fatodo.model.dto.event;

import com.persoff68.fatodo.model.constant.EventType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventDTO {

    private List<UUID> userIds;

    private EventType type;

    private String payload;

    private UUID userId;

    private Date date;

    public EventDTO(List<UUID> userIds, EventType type, String payload, UUID userId) {
        this.userIds = userIds;
        this.type = type;
        this.payload = payload;
        this.userId = userId;
        this.date = new Date();
    }

}
