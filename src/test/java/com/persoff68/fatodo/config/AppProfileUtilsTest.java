package com.persoff68.fatodo.config;

import com.persoff68.fatodo.config.constant.Profile;
import org.junit.jupiter.api.Test;

import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;

class AppProfileUtilsTest {

    @Test
    void testGetDefaultProfile() {
        Properties properties = AppProfileUtils.getDefaultProfile();
        String[] defaultProfiles = (String[]) properties.get("spring.profiles.default");
        assertThat(defaultProfiles)
                .hasSize(1)
                .contains(Profile.Constants.DEVELOPMENT_VALUE);
    }
}
