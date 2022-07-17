package com.persoff68.fatodo.client.configuration;

import com.persoff68.fatodo.config.AppProperties;
import com.persoff68.fatodo.security.jwt.JwtTokenProvider;
import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;

public class FeignSystemConfiguration {

    @Bean
    public RequestInterceptor requestInterceptor(AppProperties appProperties, JwtTokenProvider jwtTokenProvider) {
        return requestTemplate -> {
            String jwt = jwtTokenProvider.createSystemJwt();
            String header = appProperties.getAuth().getAuthorizationHeader();
            String value = appProperties.getAuth().getAuthorizationPrefix() + " " + jwt;
            requestTemplate.header(header, value);
        };
    }

}
