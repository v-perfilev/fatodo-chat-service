package com.persoff68.fatodo.builder;

import com.persoff68.fatodo.model.Message;
import com.persoff68.fatodo.model.Status;
import com.persoff68.fatodo.model.constant.StatusType;
import lombok.Builder;

import java.util.UUID;

public class TestStatus extends Status {

    @Builder
    public TestStatus(Message message, UUID userId, StatusType type) {
        super();
        super.setMessage(message);
        super.setUserId(userId);
        super.setType(type);
    }

    public static TestStatusBuilder defaultBuilder() {
        return TestStatus.builder()
                .type(StatusType.READ);
    }

    public Status toParent() {
        Status status = new Status();
        status.setUserId(getUserId());
        status.setMessage(getMessage());
        status.setType(getType());
        status.setTimestamp(getTimestamp());
        return status;
    }

}
