package com.persoff68.fatodo.web.rest.exception.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.persoff68.fatodo.exception.attribute.AttributeHandler;
import com.persoff68.fatodo.web.rest.exception.InvalidFormException;
import lombok.RequiredArgsConstructor;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE + 10)
@RequiredArgsConstructor
public class RestExceptionHandlerSet {

    private final ObjectMapper objectMapper;

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleMethodArgumentNotValidException(HttpServletRequest request) throws IOException {
        return handleInvalidFormException(request);
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<String> handleBindException(HttpServletRequest request) throws IOException {
        return handleInvalidFormException(request);
    }

    private ResponseEntity<String> handleInvalidFormException(HttpServletRequest request) throws IOException {
        Exception e = new InvalidFormException();
        return AttributeHandler.from(request, e).getResponseEntity(objectMapper);
    }

}
