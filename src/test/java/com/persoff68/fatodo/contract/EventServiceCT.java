package com.persoff68.fatodo.contract;

import com.persoff68.fatodo.client.EventServiceClient;
import com.persoff68.fatodo.model.dto.CreateChatEventDTO;
import com.persoff68.fatodo.model.dto.DeleteUserEventsDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.stubrunner.spring.AutoConfigureStubRunner;
import org.springframework.cloud.contract.stubrunner.spring.StubRunnerProperties;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SpringBootTest
@AutoConfigureStubRunner(ids = {"com.persoff68.fatodo:eventservice:+:stubs"},
        stubsMode = StubRunnerProperties.StubsMode.REMOTE)
class EventServiceCT {

    @Autowired
    EventServiceClient eventServiceClient;

    @Test
    void testAddChatEvent() {
        UUID userId1 = UUID.randomUUID();
        UUID userId2 = UUID.randomUUID();
        UUID chatId = UUID.randomUUID();
        List<UUID> recipientIdList = List.of(userId1, userId2);
        List<UUID> userIdList = List.of(userId2);
        CreateChatEventDTO dto = CreateChatEventDTO.chatCreate(recipientIdList, chatId, userId1, userIdList);
        assertDoesNotThrow(() -> eventServiceClient.addChatEvent(dto));
    }

    @Test
    void testDeleteChatEventsForUser() {
        UUID chatId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        DeleteUserEventsDTO dto = new DeleteUserEventsDTO(chatId, List.of(userId));
        assertDoesNotThrow(() -> eventServiceClient.deleteChatEventsForUser(dto));
    }

}
