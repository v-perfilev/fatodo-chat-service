package com.persoff68.fatodo.builder;

import com.persoff68.fatodo.model.Status;
import com.persoff68.fatodo.model.constant.StatusType;
import lombok.Builder;

import java.util.UUID;

public class TestStatus extends Status {

    @Builder
    public TestStatus(UUID messageId, UUID userId, StatusType type) {
        super(messageId, userId, type);
    }

    public static TestStatus.TestStatusBuilder defaultBuilder() {
        return TestStatus.builder()
                .type(StatusType.READ);
    }

    public Status toParent() {
        Status status = new Status();
        status.setUserId(getUserId());
        status.setMessageId(getMessageId());
        status.setType(getType());
        status.setTimestamp(getTimestamp());
        return status;
    }

}
