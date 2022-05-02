package com.persoff68.fatodo.exception;

import com.persoff68.fatodo.security.exception.ForbiddenException;
import com.persoff68.fatodo.security.exception.UnauthorizedException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;

class SecurityExceptionTest {

    @Test
    void testForbiddenException() {
        Object exception = new ForbiddenException();
        assertThat(exception).isInstanceOf(AbstractException.class);
        AbstractException abstractException = (AbstractException) exception;
        assertThat(abstractException.getStatus()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(abstractException.getFeedBackCode()).isEqualTo("security.forbidden");
    }

    @Test
    void testUnauthorizedException() {
        Object exception = new UnauthorizedException();
        assertThat(exception).isInstanceOf(AbstractException.class);
        AbstractException abstractException = (AbstractException) exception;
        assertThat(abstractException.getStatus()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(abstractException.getFeedBackCode()).isEqualTo("security.unauthorized");
    }

}
