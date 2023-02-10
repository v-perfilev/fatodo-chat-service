package com.persoff68.fatodo.service.exception;

import org.springframework.http.HttpStatus;

public final class DatabaseException extends AbstractDatabaseException {
    private static final String MESSAGE = "Database error";
    private static final String FEEDBACK_CODE = "model.databaseError";

    public DatabaseException() {
        super(HttpStatus.INTERNAL_SERVER_ERROR, MESSAGE, FEEDBACK_CODE);
    }

}
