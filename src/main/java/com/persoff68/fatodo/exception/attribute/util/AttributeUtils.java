package com.persoff68.fatodo.exception.attribute.util;

import com.persoff68.fatodo.exception.AbstractException;
import org.springframework.http.HttpStatus;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import javax.servlet.http.HttpServletRequest;

public class AttributeUtils {
    private static final String EXCEPTION_PATH = "javax.servlet.error.exception";
    private static final String STATUS_CODE_PATH = "javax.servlet.error.status_code";
    private static final String MESSAGE_PATH = "javax.servlet.error.message";

    private AttributeUtils() {
    }

    public static Throwable getError(WebRequest webRequest) {
        return getAttribute(webRequest, EXCEPTION_PATH);
    }

    public static String getMessage(WebRequest webRequest) {
        return getAttribute(webRequest, MESSAGE_PATH);
    }

    public static HttpStatus getStatus(HttpServletRequest request) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

        Throwable error = getError(new ServletWebRequest(request));
        Integer statusCode = (Integer) request.getAttribute(STATUS_CODE_PATH);

        if (error instanceof AbstractException e) {
            status = e.getStatus();
        } else if (statusCode != null) {
            try {
                status = HttpStatus.valueOf(statusCode);
            } catch (Exception ex) {
                // skip
            }
        }
        return status;
    }

    @SuppressWarnings("unchecked")
    public static <T> T getAttribute(RequestAttributes requestAttributes, String name) {
        return (T) requestAttributes.getAttribute(name, RequestAttributes.SCOPE_REQUEST);
    }
}
