package com.persoff68.fatodo.service.exception;

import com.persoff68.fatodo.exception.AbstractException;
import org.springframework.http.HttpStatus;

public class PermissionException extends AbstractException {
    private static final String MESSAGE = "Permission restricted";
    private static final String FEEDBACK_CODE = "permission.restricted";

    public PermissionException() {
        super(HttpStatus.FORBIDDEN, MESSAGE, FEEDBACK_CODE);
    }

    public PermissionException(String message) {
        super(HttpStatus.FORBIDDEN, message, FEEDBACK_CODE);
    }

}
