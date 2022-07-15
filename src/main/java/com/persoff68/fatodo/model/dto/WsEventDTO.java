package com.persoff68.fatodo.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
public class WsEventDTO<T> {

    private final List<UUID> userIds;

    private final T content;

}
