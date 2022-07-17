package com.persoff68.fatodo.client.configuration;

import com.persoff68.fatodo.config.AppProperties;
import com.persoff68.fatodo.security.exception.UnauthorizedException;
import com.persoff68.fatodo.security.util.SecurityUtils;
import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;

public class FeignAuthConfiguration {

    @Bean
    public RequestInterceptor requestInterceptor(AppProperties appProperties) {
        return requestTemplate -> {
            String jwt = SecurityUtils.getCurrentJwt()
                    .orElseThrow(UnauthorizedException::new);
            String header = appProperties.getAuth().getAuthorizationHeader();
            String value = appProperties.getAuth().getAuthorizationPrefix() + " " + jwt;
            requestTemplate.header(header, value);
        };
    }

}
