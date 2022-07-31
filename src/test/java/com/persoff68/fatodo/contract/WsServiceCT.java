package com.persoff68.fatodo.contract;

import com.persoff68.fatodo.builder.TestChatDTO;
import com.persoff68.fatodo.builder.TestMessageDTO;
import com.persoff68.fatodo.builder.TestReactionsDTO;
import com.persoff68.fatodo.builder.TestStatusesDTO;
import com.persoff68.fatodo.builder.TestWsEventDTO;
import com.persoff68.fatodo.client.WsServiceClient;
import com.persoff68.fatodo.model.dto.ChatDTO;
import com.persoff68.fatodo.model.dto.MessageDTO;
import com.persoff68.fatodo.model.dto.ReactionsDTO;
import com.persoff68.fatodo.model.dto.StatusesDTO;
import com.persoff68.fatodo.model.dto.WsEventDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.stubrunner.spring.AutoConfigureStubRunner;
import org.springframework.cloud.contract.stubrunner.spring.StubRunnerProperties;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SpringBootTest
@AutoConfigureStubRunner(ids = {"com.persoff68.fatodo:wsservice:+:stubs"}, stubsMode =
        StubRunnerProperties.StubsMode.REMOTE)
class WsServiceCT {

    @Autowired
    WsServiceClient wsServiceClient;

    @Test
    void testSendChatNewEvent() {
        ChatDTO chatDTO = TestChatDTO.defaultBuilder().build().toParent();
        WsEventDTO<ChatDTO> dto = TestWsEventDTO.<ChatDTO>defaultBuilder().content(chatDTO).build().toParent();
        assertDoesNotThrow(() -> wsServiceClient.sendChatNewEvent(dto));
    }

    @Test
    void testSendChatUpdateEvent() {
        ChatDTO chatDTO = TestChatDTO.defaultBuilder().build().toParent();
        WsEventDTO<ChatDTO> dto = TestWsEventDTO.<ChatDTO>defaultBuilder().content(chatDTO).build().toParent();
        assertDoesNotThrow(() -> wsServiceClient.sendChatUpdateEvent(dto));
    }

    @Test
    void testSendChatDeleteEvent() {
        WsEventDTO<UUID> dto = TestWsEventDTO.<UUID>defaultBuilder().content(UUID.randomUUID()).build().toParent();
        assertDoesNotThrow(() -> wsServiceClient.sendChatDeleteEvent(dto));
    }

    @Test
    void testSendChatLastMessageEvent() {
        ChatDTO chatDTO = TestChatDTO.defaultBuilder().build().toParent();
        WsEventDTO<ChatDTO> dto = TestWsEventDTO.<ChatDTO>defaultBuilder().content(chatDTO).build().toParent();
        assertDoesNotThrow(() -> wsServiceClient.sendChatLastMessageEvent(dto));
    }

    @Test
    void testSendChatLastMessageUpdateEvent() {
        ChatDTO chatDTO = TestChatDTO.defaultBuilder().build().toParent();
        WsEventDTO<ChatDTO> dto = TestWsEventDTO.<ChatDTO>defaultBuilder().content(chatDTO).build().toParent();
        assertDoesNotThrow(() -> wsServiceClient.sendChatLastMessageUpdateEvent(dto));
    }

    @Test
    void testSendMessageNewEvent() {
        MessageDTO messageDTO = TestMessageDTO.defaultBuilder().build().toParent();
        WsEventDTO<MessageDTO> dto = TestWsEventDTO.<MessageDTO>defaultBuilder().content(messageDTO).build().toParent();
        assertDoesNotThrow(() -> wsServiceClient.sendMessageNewEvent(dto));
    }

    @Test
    void testSendMessageUpdateEvent() {
        MessageDTO messageDTO = TestMessageDTO.defaultBuilder().build().toParent();
        WsEventDTO<MessageDTO> dto = TestWsEventDTO.<MessageDTO>defaultBuilder().content(messageDTO).build().toParent();
        assertDoesNotThrow(() -> wsServiceClient.sendMessageUpdateEvent(dto));
    }

    @Test
    void testSendStatusesEvent() {
        StatusesDTO statusesDTO = TestStatusesDTO.defaultBuilder().build().toParent();
        WsEventDTO<StatusesDTO> dto =
                TestWsEventDTO.<StatusesDTO>defaultBuilder().content(statusesDTO).build().toParent();
        assertDoesNotThrow(() -> wsServiceClient.sendStatusesEvent(dto));
    }

    @Test
    void testSendReactionsEvent() {
        ReactionsDTO reactionsDTO = TestReactionsDTO.defaultBuilder().build().toParent();
        WsEventDTO<ReactionsDTO> dto =
                TestWsEventDTO.<ReactionsDTO>defaultBuilder().content(reactionsDTO).build().toParent();
        assertDoesNotThrow(() -> wsServiceClient.sendReactionsEvent(dto));
    }

}
