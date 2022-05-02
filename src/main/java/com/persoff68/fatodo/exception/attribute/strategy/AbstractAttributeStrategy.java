package com.persoff68.fatodo.exception.attribute.strategy;

import com.persoff68.fatodo.exception.AbstractException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

@RequiredArgsConstructor
public abstract class AbstractAttributeStrategy implements AttributeStrategy {

    protected final Exception exception;

    @Getter
    protected final Map<String, Object> errorAttributes = new LinkedHashMap<>();

    @Override
    public HttpStatus getStatus() {
        return exception instanceof AbstractException e && e.getStatus() != null ? e.getStatus() :
                HttpStatus.INTERNAL_SERVER_ERROR;
    }

    @Override
    public String getFeedbackCode() {
        return exception instanceof AbstractException e && e.getFeedBackCode() != null ? e.getFeedBackCode() : null;
    }

    @Override
    public void addTimestamp() {
        errorAttributes.put("timestamp", new Date());
    }

    @Override
    public void addStatus() {
        HttpStatus status = getStatus();
        errorAttributes.put("status", status.value());
        errorAttributes.put("error", status.getReasonPhrase());
    }

    @Override
    public void addFeedbackCode() {
        String feedbackCode = getFeedbackCode();
        if (feedbackCode != null) {
            errorAttributes.put("feedbackCode", feedbackCode);
        }
    }

    @Override
    public void addErrorDetails() {
        String message = exception.getMessage();
        if (message != null) {
            errorAttributes.put("message", message);
        }
    }
}
