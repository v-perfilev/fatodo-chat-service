package com.persoff68.fatodo.service.exception;

import com.persoff68.fatodo.exception.AbstractException;
import org.springframework.http.HttpStatus;

public abstract class AbstractDatabaseException extends AbstractException {

    protected AbstractDatabaseException(HttpStatus status, String message, String feedback) {
        super(status, message, feedback);
    }

}
