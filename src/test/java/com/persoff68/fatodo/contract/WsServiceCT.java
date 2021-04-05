package com.persoff68.fatodo.contract;

import com.persoff68.fatodo.builder.TestWsChatEventDTO;
import com.persoff68.fatodo.builder.TestWsMessageEventDTO;
import com.persoff68.fatodo.builder.TestWsReactionsEventDTO;
import com.persoff68.fatodo.builder.TestWsStatusesEventDTO;
import com.persoff68.fatodo.client.WsServiceClient;
import com.persoff68.fatodo.model.dto.WsChatEventDTO;
import com.persoff68.fatodo.model.dto.WsMessageEventDTO;
import com.persoff68.fatodo.model.dto.WsReactionsEventDTO;
import com.persoff68.fatodo.model.dto.WsStatusesEventDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.stubrunner.spring.AutoConfigureStubRunner;
import org.springframework.cloud.contract.stubrunner.spring.StubRunnerProperties;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureStubRunner(ids = {"com.persoff68.fatodo:wsservice:+:stubs"},
        stubsMode = StubRunnerProperties.StubsMode.REMOTE)
public class WsServiceCT {

    @Autowired
    WsServiceClient wsServiceClient;

    @Test
    void testSendChatNewEvent() {
        WsChatEventDTO dto = TestWsChatEventDTO.defaultBuilder().build().toParent();
        wsServiceClient.sendChatNewEvent(dto);
        assertThat(true).isTrue();
    }

    @Test
    void testSendChatUpdateEvent() {
        WsChatEventDTO dto = TestWsChatEventDTO.defaultBuilder().build().toParent();
        wsServiceClient.sendChatUpdateEvent(dto);
        assertThat(true).isTrue();
    }

    @Test
    void testSendChatLastMessageEvent() {
        WsChatEventDTO dto = TestWsChatEventDTO.defaultBuilder().build().toParent();
        wsServiceClient.sendChatLastMessageEvent(dto);
        assertThat(true).isTrue();
    }

    @Test
    void testSendChatLastMessageUpdateEvent() {
        WsChatEventDTO dto = TestWsChatEventDTO.defaultBuilder().build().toParent();
        wsServiceClient.sendChatLastMessageUpdateEvent(dto);
        assertThat(true).isTrue();
    }

    @Test
    void testSendMessageNewEvent() {
        WsMessageEventDTO dto = TestWsMessageEventDTO.defaultBuilder().build().toParent();
        wsServiceClient.sendMessageNewEvent(dto);
        assertThat(true).isTrue();
    }

    @Test
    void testSendMessageUpdateEvent() {
        WsMessageEventDTO dto = TestWsMessageEventDTO.defaultBuilder().build().toParent();
        wsServiceClient.sendMessageUpdateEvent(dto);
        assertThat(true).isTrue();
    }

    @Test
    void testSendStatusesEvent() {
        WsStatusesEventDTO dto = TestWsStatusesEventDTO.defaultBuilder().build().toParent();
        wsServiceClient.sendStatusesEvent(dto);
        assertThat(true).isTrue();
    }

    @Test
    void testSendReactionsEvent() {
        WsReactionsEventDTO dto = TestWsReactionsEventDTO.defaultBuilder().build().toParent();
        wsServiceClient.sendReactionsEvent(dto);
        assertThat(true).isTrue();
    }

}
