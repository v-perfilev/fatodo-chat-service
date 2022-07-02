package com.persoff68.fatodo.config;

import com.persoff68.fatodo.client.ContactServiceClient;
import com.persoff68.fatodo.client.UserServiceClient;
import com.persoff68.fatodo.client.WsServiceClient;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
@RequiredArgsConstructor
public class ClientConfiguration {

    private final BeanFactory beanFactory;

    @Bean
    @Primary
    public ContactServiceClient itemClient() {
        return (ContactServiceClient) beanFactory.getBean("contactServiceClientWrapper");
    }

    @Bean
    @Primary
    public UserServiceClient userClient() {
        return (UserServiceClient) beanFactory.getBean("userServiceClientWrapper");
    }

    @Bean
    @Primary
    public WsServiceClient wsClient() {
        return (WsServiceClient) beanFactory.getBean("wsServiceClientWrapper");
    }

}
