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
import com.persoff68.fatodo.model.StatusType;
import com.persoff68.fatodo.repository.ChatRepository;
import com.persoff68.fatodo.repository.MessageRepository;
import com.persoff68.fatodo.repository.StatusRepository;
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

import static org.assertj.core.api.Assertions.assertThat;
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

    private Message message1;
    private Message message2;
    private Message message3;
    private Message message4;

    @Autowired
    WebApplicationContext context;
    @Autowired
    ChatRepository chatRepository;
    @Autowired
    MessageRepository messageRepository;
    @Autowired
    StatusRepository statusRepository;
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
        statusRepository.deleteAll();

        Chat chat1 = createDirectChat(USER_ID_1, USER_ID_2);
        message1 = createMessage(chat1, USER_ID_2);
        message2 = createMessage(chat1, USER_ID_1);
        message3 = createMessage(chat1, USER_ID_2);
        createStatuses(message3.getId(), USER_ID_1);

        Chat chat2 = createDirectChat(USER_ID_2, USER_ID_3);
        message4 = createMessage(chat2, USER_ID_2);

        when(userServiceClient.doesIdExist(any())).thenReturn(true);
    }

    @Test
    @WithCustomSecurityContext(id = USER_ID_1)
    void testSetRead_ok() throws Exception {
        String messageId = message1.getId().toString();
        String url = ENDPOINT + "/" + messageId + "/read";
        mvc.perform(get(url))
                .andExpect(status().isOk());
        List<Status> statusList = statusRepository.findAll();
        boolean statusExists = statusList.stream()
                .anyMatch(status -> status.getMessageId().toString().equals(messageId)
                        && status.getUserId().toString().equals(USER_ID_1)
                        && status.getType().equals(StatusType.READ));
        assertThat(statusExists).isTrue();
    }


    private Chat createDirectChat(String... userIds) {
        List<MemberEvent> memberEventList = Arrays.stream(userIds)
                .map(id -> TestMemberEvent.defaultBuilder()
                        .userId(UUID.fromString(id)).build().toParent())
                .collect(Collectors.toList());

        Chat chat = TestChat.defaultBuilder().memberEvents(memberEventList).build().toParent();
        return chatRepository.save(chat);
    }

    private Message createMessage(Chat chat, String userId) {
        Message message = TestMessage.defaultBuilder()
                .chat(chat).userId(UUID.fromString(userId))
                .build().toParent();
        return messageRepository.save(message);
    }

    private void createStatuses(UUID messageId, String... readUserIds) {
        List<Status> statusList = Arrays.stream(readUserIds)
                .map(id -> TestStatus.defaultBuilder()
                        .messageId(messageId).userId(UUID.fromString(id)).build().toParent())
                .collect(Collectors.toList());
        statusRepository.saveAll(statusList);
    }
}