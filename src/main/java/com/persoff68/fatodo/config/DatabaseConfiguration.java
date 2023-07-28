package com.persoff68.fatodo.config;

import com.persoff68.fatodo.config.constant.AppConstants;
import liquibase.integration.spring.SpringLiquibase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;

@Configuration
@EnableJpaRepositories(basePackages = AppConstants.REPOSITORY_PATH)
@EnableJpaAuditing(auditorAwareRef = "securityAuditorAware")
@EnableTransactionManagement
@RequiredArgsConstructor
@Slf4j
public class DatabaseConfiguration {

    private final AppProperties appProperties;
    private final DataSource dataSource;

    @Bean
    public SpringLiquibase liquibase() {
        removeExpiredLocks();
        SpringLiquibase liquibase = new SpringLiquibase();
        liquibase.setChangeLog("classpath:db/master.xml");
        liquibase.setDataSource(dataSource);
        return liquibase;
    }

    private void removeExpiredLocks() {
        long timeoutInMillis = appProperties.getDb().getLiquibaseLockTimeoutSec() * 1000;
        Timestamp lastLockTime = new Timestamp(System.currentTimeMillis() - timeoutInMillis);
        String queryTemplate = "DELETE FROM DATABASECHANGELOGLOCK WHERE LOCKED=true AND LOCKGRANTED<'%s'";
        String query = String.format(queryTemplate, lastLockTime);

        try (Connection connection = dataSource.getConnection()) {
            Statement statement = connection.createStatement();
            int updateCount = statement.executeUpdate(query);
            if (updateCount > 0) {
                log.warn("Liquibase locks removed: {}.", updateCount);
            } else {
                log.info("No liquibase locks removed");
            }
        } catch (SQLException e) {
            log.error("Remove liquibase lock threw and exception. ", e);
        }
    }

}
