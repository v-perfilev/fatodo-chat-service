package com.persoff68.fatodo.model.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.UUID;

@Data
public abstract class AbstractDTO implements Serializable {

    protected UUID id;

}
