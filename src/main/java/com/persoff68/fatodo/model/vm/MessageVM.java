package com.persoff68.fatodo.model.vm;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.UUID;

@Data
public class MessageVM {

    @NotEmpty
    private String text;

    private UUID referenceId;

}
