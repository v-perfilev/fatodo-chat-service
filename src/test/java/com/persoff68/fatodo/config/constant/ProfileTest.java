package com.persoff68.fatodo.config.constant;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ProfileTest {

    @Test
    void testEnum() {
        assertThat(Profile.DEVELOPMENT.getValue()).isEqualTo("dev");
        assertThat(Profile.DOCKER.getValue()).isEqualTo("docker");
        assertThat(Profile.STAGING.getValue()).isEqualTo("stage");
        assertThat(Profile.PRODUCTION.getValue()).isEqualTo("prod");
        assertThat(Profile.TEST.getValue()).isEqualTo("test");
    }

}
