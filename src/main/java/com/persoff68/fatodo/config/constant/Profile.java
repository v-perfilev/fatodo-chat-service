package com.persoff68.fatodo.config.constant;

import lombok.Getter;

public enum Profile {
    DEVELOPMENT(Constants.DEVELOPMENT_VALUE),
    DOCKER(Constants.DOCKER_VALUE),
    STAGING(Constants.STAGING_VALUE),
    PRODUCTION(Constants.PRODUCTION_VALUE),
    TEST(Constants.TEST_VALUE);

    @Getter
    private final String value;

    Profile(String value) {
        this.value = value;
    }

    public static class Constants {
        public static final String DEVELOPMENT_VALUE = "dev";
        public static final String DOCKER_VALUE = "docker";
        public static final String STAGING_VALUE = "stage";
        public static final String PRODUCTION_VALUE = "prod";
        public static final String TEST_VALUE = "test";

        private Constants() {
        }
    }
}
