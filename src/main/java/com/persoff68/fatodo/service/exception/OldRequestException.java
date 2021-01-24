package com.persoff68.fatodo.service.exception;

import com.persoff68.fatodo.exception.AbstractException;
import org.springframework.http.HttpStatus;

public class OldRequestException extends AbstractException {
    private static final String MESSAGE = "Old request";
    private static final String FEEDBACK_CODE = "timeout.old";

    public OldRequestException() {
        super(HttpStatus.BAD_REQUEST, MESSAGE, FEEDBACK_CODE);
    }

}
