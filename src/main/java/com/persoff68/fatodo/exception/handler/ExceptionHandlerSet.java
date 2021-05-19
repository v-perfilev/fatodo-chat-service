package com.persoff68.fatodo.exception.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.persoff68.fatodo.exception.AbstractException;
import com.persoff68.fatodo.exception.attribute.AttributeHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE + 100)
@RequiredArgsConstructor
public class ExceptionHandlerSet {

    private final ObjectMapper objectMapper;

    @ExceptionHandler(AbstractException.class)
    public ResponseEntity<String> handleAbstractException(HttpServletRequest request, AbstractException e)
            throws IOException {
        return AttributeHandler.from(request, e).getResponseEntity(objectMapper);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntimeException(HttpServletRequest request, RuntimeException e)
            throws IOException {
        return e.getCause() instanceof Exception
                ? AttributeHandler.from(request, (Exception) e.getCause()).getResponseEntity(objectMapper)
                : AttributeHandler.from(request, e).getResponseEntity(objectMapper);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(HttpServletRequest request, Exception e)
            throws IOException {
        return AttributeHandler.from(request, e).getResponseEntity(objectMapper);
    }

}
