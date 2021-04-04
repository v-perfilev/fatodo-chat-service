package com.persoff68.fatodo.contract;

import com.persoff68.fatodo.client.UserServiceClient;
import com.persoff68.fatodo.client.WsServiceClient;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.contract.verifier.messaging.boot.AutoConfigureMessageVerifier;
import org.springframework.web.context.WebApplicationContext;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMessageVerifier
public abstract class ContractBase {

    @Autowired
    WebApplicationContext context;

    @MockBean
    UserServiceClient userServiceClient;
    @MockBean
    WsServiceClient wsServiceClient;

    @BeforeEach
    public void setup() {
        RestAssuredMockMvc.webAppContextSetup(context);

        when(userServiceClient.doesIdExist(any())).thenReturn(true);

        doNothing().when(wsServiceClient).sendChatNewEvent(any());
        doNothing().when(wsServiceClient).sendChatUpdateEvent(any());
        doNothing().when(wsServiceClient).sendChatLastMessageEvent(any());
        doNothing().when(wsServiceClient).sendChatLastMessageUpdateEvent(any());
        doNothing().when(wsServiceClient).sendMessageNewEvent(any());
        doNothing().when(wsServiceClient).sendMessageUpdateEvent(any());
        doNothing().when(wsServiceClient).sendStatusesEvent(any());
        doNothing().when(wsServiceClient).sendReactionsEvent(any());
    }

}
