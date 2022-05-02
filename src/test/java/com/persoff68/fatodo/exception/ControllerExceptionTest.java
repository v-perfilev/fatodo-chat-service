package com.persoff68.fatodo.exception;

import com.persoff68.fatodo.web.rest.exception.InvalidFormException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;

class ControllerExceptionTest {

    @Test
    void testInvalidFormException() {
        Object exception = new InvalidFormException();
        assertThat(exception).isInstanceOf(AbstractException.class);
        AbstractException abstractException = (AbstractException) exception;
        assertThat(abstractException.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(abstractException.getFeedBackCode()).isEqualTo("form.invalid");
    }

}
