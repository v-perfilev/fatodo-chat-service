package com.persoff68.fatodo.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;

class CommonExceptionTest {

    @Test
    void testClientException() {
        Object exception = new ClientException();
        assertThat(exception).isInstanceOf(AbstractException.class);
        AbstractException abstractException = (AbstractException) exception;
        assertThat(abstractException.getStatus()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
