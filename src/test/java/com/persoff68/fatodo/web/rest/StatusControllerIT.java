package com.persoff68.fatodo.web.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.persoff68.fatodo.FatodoMessageServiceApplication;
import com.persoff68.fatodo.annotation.WithCustomSecurityContext;
import com.persoff68.fatodo.builder.TestChat;
import com.persoff68.fatodo.builder.TestMemberEvent;
import com.persoff68.fatodo.builder.TestMessage;
import com.persoff68.fatodo.builder.TestStatus;
import com.persoff68.fatodo.client.UserServiceClient;
import com.persoff68.fatodo.model.Chat;
import com.persoff68.fatodo.model.MemberEvent;
import com.persoff68.fatodo.model.Message;
import com.persoff68.fatodo.model.Status;
import com.persoff68.fatodo.repository.ChatRepository;
import com.persoff68.fatodo.repository.MessageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = FatodoMessageServiceApplication.class)
public class StatusControllerIT {
    private static final String ENDPOINT = "/api/status";

    private static final String USER_ID_1 = "3c300277-b5ea-48d1-80db-ead620cf5846";
    private static final String USER_ID_2 = "357a2a99-7b7e-4336-9cd7-18f2cf73fab9";
    private static final String USER_ID_3 = "71bae736-415b-474c-9865-29043cbc8d0c";

    private static final String MESSAGE_ID_1 = "d17c5f24-4ba4-47e4-8f93-11f098e93b3c";
    private static final String MESSAGE_ID_2 = "3f4ed8ad-eecc-49c9-a6ad-cfd81a0e4847";
    private static final String MESSAGE_ID_3 = "b8685cb0-b3c3-4ed4-9ecc-e803d20cdcef";
    private static final String MESSAGE_ID_4 = "4d50aca1-6003-43e3-8237-385854a0557e";

    @Autowired
    WebApplicationContext context;
    @Autowired
    ChatRepository chatRepository;
    @Autowired
    MessageRepository messageRepository;
    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    UserServiceClient userServiceClient;

    MockMvc mvc;

    @BeforeEach
    public void setup() {
        mvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();

        chatRepository.deleteAll();
        messageRepository.deleteAll();

        Chat chat1 = createDirectChat(USER_ID_1, USER_ID_2);
        createMessage(chat1, USER_ID_1, MESSAGE_ID_1);
        createMessage(chat1, USER_ID_2, MESSAGE_ID_2);
        createMessage(chat1, USER_ID_2, MESSAGE_ID_3, USER_ID_1);

        Chat chat2 = createDirectChat(USER_ID_2, USER_ID_3);
        createMessage(chat2, USER_ID_2, MESSAGE_ID_4);

        when(userServiceClient.doesIdExist(any())).thenReturn(true);
    }

    @Test
    @WithCustomSecurityContext(id = USER_ID_1)
    void testSetRead_ok() throws Exception {
        String url = ENDPOINT + "/" + MESSAGE_ID_1 + "/read";
        mvc.perform(get(url))
                .andExpect(status().isOk());
    }


    private Chat createDirectChat(String... userIds) {
        List<MemberEvent> memberEventList = Arrays.stream(userIds)
                .map(id -> TestMemberEvent.defaultBuilder()
                        .userId(UUID.fromString(id)).build().toParent())
                .collect(Collectors.toList());

        Chat chat = TestChat.defaultBuilder().memberEvents(memberEventList).build().toParent();
        return chatRepository.save(chat);
    }

    private void createMessage(Chat chat, String userId, String messageId, String... readUserIds) {
        List<Status> statusList = Arrays.stream(readUserIds)
                .map(id -> TestStatus.defaultBuilder()
                        .userId(UUID.fromString(id)).build().toParent())
                .collect(Collectors.toList());
        Message message = TestMessage.defaultBuilder()
                .id(UUID.fromString(messageId)).chat(chat).userId(UUID.fromString(userId))
                .statuses(statusList).build().toParent();
        messageRepository.save(message);
    }

}
