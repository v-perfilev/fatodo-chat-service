package com.persoff68.fatodo.config.onstart;

import com.persoff68.fatodo.config.AppProperties;
import com.persoff68.fatodo.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.web.context.WebServerInitializedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;
import java.util.stream.Stream;

@Component
@Profile("!test")
@RequiredArgsConstructor
@Slf4j
public class OnStartRunner implements ApplicationRunner, ApplicationListener<WebServerInitializedEvent> {

    private final Environment env;
    private final AppProperties appProperties;
    private final JwtTokenProvider jwtTokenProvider;

    private String appName;
    private String protocol;
    private String host;
    private String port;
    private String[] profiles;

    @Override
    public void onApplicationEvent(WebServerInitializedEvent event) {
        try {
            this.appName = env.getProperty("spring.application.name");
            this.protocol = "http";
            this.host = "localhost";
            this.port = env.getProperty("server.port");
            this.profiles = env.getActiveProfiles().length > 0 ? env.getActiveProfiles() : env.getDefaultProfiles();
        } catch (Exception e) {
            // skip on start
        }
    }

    @Override
    public void run(ApplicationArguments args) {
        boolean allNotNull = Stream.of(appName, protocol, host, port, profiles).allMatch(Objects::nonNull);
        if (allNotNull) {
            logCommonInfo();
            warmUp();
        }
    }

    private void logCommonInfo() {
        log.info(
                """
                            Application '{}' is running!
                            \tAccess URL: \t{}://{}:{}
                            \tProfile(s): \t{}
                        """,
                appName,
                protocol,
                host,
                port,
                profiles
        );
    }

    private void warmUp() {
        RestTemplate restTemplate = new RestTemplate();
        String url = protocol + "://" + host + ":" + port + WarmUpController.ENDPOINT;
        HttpEntity<?> request = createSystemRequestEntity();
        restTemplate.exchange(url, HttpMethod.GET, request, Void.class);
        log.info("Application warmed up");
    }

    private HttpEntity<?> createSystemRequestEntity() {
        String jwt = jwtTokenProvider.createSystemJwt();
        String header = appProperties.getAuth().getAuthorizationHeader();
        String value = appProperties.getAuth().getAuthorizationPrefix() + " " + jwt;

        HttpHeaders headers = new HttpHeaders();
        headers.set(header, value);

        return new HttpEntity<>(headers);
    }
}
