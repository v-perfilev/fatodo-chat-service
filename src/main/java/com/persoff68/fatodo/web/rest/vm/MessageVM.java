package com.persoff68.fatodo.web.rest.vm;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageVM {

    private UUID id;

    @NotNull
    private UUID recipientId;

    @NotNull
    private String text;

}
