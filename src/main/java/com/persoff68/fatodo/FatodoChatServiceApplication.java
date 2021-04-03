package com.persoff68.fatodo;

import com.persoff68.fatodo.config.AppProfileUtils;
import com.persoff68.fatodo.config.AppProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import java.util.Properties;

@SpringBootApplication
@EnableConfigurationProperties(AppProperties.class)
public class FatodoChatServiceApplication {

    public static void main(String[] args) {
        Properties defaultProfileProperties = AppProfileUtils.getDefaultProfile();
        SpringApplication app = new SpringApplication(FatodoChatServiceApplication.class);
        app.setDefaultProperties(defaultProfileProperties);
        app.run();
    }

}
