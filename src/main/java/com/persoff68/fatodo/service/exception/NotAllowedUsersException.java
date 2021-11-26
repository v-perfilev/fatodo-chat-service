package com.persoff68.fatodo.service.exception;

import org.springframework.http.HttpStatus;

public final class NotAllowedUsersException extends AbstractDatabaseException {
    private static final String MESSAGE = "Users are not in contact list";
    private static final String FEEDBACK_CODE = "permission.notAllowedUsers";

    public NotAllowedUsersException() {
        super(HttpStatus.FORBIDDEN, MESSAGE, FEEDBACK_CODE);
    }


}
