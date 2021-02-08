package com.persoff68.fatodo.builder;

import com.persoff68.fatodo.model.Chat;
import com.persoff68.fatodo.model.Message;
import com.persoff68.fatodo.model.Reaction;
import com.persoff68.fatodo.model.Status;
import com.persoff68.fatodo.model.StatusType;
import lombok.Builder;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

public class TestStatus extends Status {
    private static final String DEFAULT_VALUE = "test_value";

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
