package com.persoff68.fatodo.model.dto;

import com.persoff68.fatodo.config.constant.AppConstants;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
public class WsEventDTO<T extends Serializable> implements Serializable {
    protected static final long serialVersionUID = AppConstants.SERIAL_VERSION_UID;

    private final List<UUID> userIds;
    private final T content;

}
