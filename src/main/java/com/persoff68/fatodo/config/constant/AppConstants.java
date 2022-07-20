package com.persoff68.fatodo.config.constant;

import java.util.UUID;

public class AppConstants {
    public static final String REPOSITORY_PATH = "com.persoff68.fatodo.repository";
    public static final String FEIGN_CLIENT_PATH = "com.persoff68.fatodo.client";
    public static final String TASK_PATH = "com.persoff68.fatodo.task";
    public static final String CONTROLLER_PATH = "com.persoff68.fatodo.web.rest";

    public static final UUID SYSTEM_ID = UUID.fromString("00000000-0000-0000-0000-000000000000");
    public static final String SYSTEM_USERNAME = "system";
    public static final String SYSTEM_AUTHORITY = AuthorityType.SYSTEM.getValue();
    public static final long SYSTEM_TOKEN_EXPIRATION_SEC = 60;

    public static final long SERIAL_VERSION_UID = 1L;

    private AppConstants() {
    }
}
