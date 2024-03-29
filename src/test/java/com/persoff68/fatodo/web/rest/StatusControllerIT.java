package com.persoff68.fatodo.web.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.persoff68.fatodo.FatodoChatServiceApplication;
import com.persoff68.fatodo.annotation.WithCustomSecurityContext;
import com.persoff68.fatodo.builder.TestChat;
import com.persoff68.fatodo.builder.TestMemberEvent;
import com.persoff68.fatodo.builder.TestMessage;
import com.persoff68.fatodo.builder.TestStatus;
import com.persoff68.fatodo.client.UserServiceClient;
import com.persoff68.fatodo.client.WsServiceClient;
import com.persoff68.fatodo.model.Chat;
import com.persoff68.fatodo.model.MemberEvent;
import com.persoff68.fatodo.model.Message;
import com.persoff68.fatodo.model.Status;
import com.persoff68.fatodo.model.constant.StatusType;
import com.persoff68.fatodo.repository.ChatRepository;
import com.persoff68.fatodo.repository.MemberEventRepository;
import com.persoff68.fatodo.repository.MessageRepository;
import com.persoff68.fatodo.repository.StatusRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = FatodoChatServiceApplication.class)
@AutoConfigureMockMvc
class StatusControllerIT {
    private static final String ENDPOINT = "/api/status";

    private static final String USER_ID_1 = "3c300277-b5ea-48d1-80db-ead620cf5846";
    private static final String USER_ID_2 = "357a2a99-7b7e-4336-9cd7-18f2cf73fab9";
    private static final String USER_ID_3 = "71bae736-415b-474c-9865-29043cbc8d0c";

    @Autowired
    MockMvc mvc;
    @Autowired
    ChatRepository chatRepository;
    @Autowired
    MemberEventRepository memberEventRepository;
    @Autowired
    MessageRepository messageRepository;
    @Autowired
    StatusRepository statusRepository;
    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    UserServiceClient userServiceClient;
    @MockBean
    WsServiceClient wsServiceClient;


    private Message message1;
    private Message message2;
    private Message message3;
    private Message message4;

    private Chat chat1;
    private Chat chat2;

    @BeforeEach
    void setup() {
        chat1 = createDirectChat();
        createMemberEvents(chat1, USER_ID_1, USER_ID_2);
        message1 = createMessage(chat1, USER_ID_2);
        message2 = createMessage(chat1, USER_ID_1);
        message3 = createMessage(chat1, USER_ID_2);
        createStatuses(message3, USER_ID_1);

        chat2 = createDirectChat();
        createMemberEvents(chat2, USER_ID_2, USER_ID_3);
        message4 = createMessage(chat2, USER_ID_2);

        when(userServiceClient.doesIdExist(any())).thenReturn(true);
    }

    @AfterEach
    void cleanup() {
        chatRepository.deleteAll();
        memberEventRepository.deleteAll();
        messageRepository.deleteAll();
        statusRepository.deleteAll();
    }

    @Test
    @WithCustomSecurityContext(id = USER_ID_1)
    void testSetMessageRead_ok() throws Exception {
        String messageId = message1.getId().toString();
        String url = ENDPOINT + "/" + messageId + "/read";
        mvc.perform(post(url))
                .andExpect(status().isCreated());
        List<Status> statusList = statusRepository.findAll();
        boolean statusExists = statusList.stream()
                .anyMatch(status -> status.getMessage().getId().toString().equals(messageId)
                        && status.getUserId().toString().equals(USER_ID_1)
                        && status.getType().equals(StatusType.READ));
        assertThat(statusExists).isTrue();
    }

    @Test
    @WithCustomSecurityContext(id = USER_ID_1)
    void testSetMessageRead_ok_ignoreIfRead() throws Exception {
        String messageId = message3.getId().toString();
        String url = ENDPOINT + "/" + messageId + "/read";
        mvc.perform(post(url))
                .andExpect(status().isCreated());
    }

    @Test
    @WithCustomSecurityContext(id = USER_ID_1)
    void testSetMessageRead_forbidden_ownMessage() throws Exception {
        String messageId = message2.getId().toString();
        String url = ENDPOINT + "/" + messageId + "/read";
        mvc.perform(post(url))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithCustomSecurityContext(id = USER_ID_1)
    void testSetMessageRead_forbidden_noPermissions() throws Exception {
        String messageId = message4.getId().toString();
        String url = ENDPOINT + "/" + messageId + "/read";
        mvc.perform(post(url))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithCustomSecurityContext(id = USER_ID_1)
    void testSetMessageRead_notFound() throws Exception {
        String messageId = UUID.randomUUID().toString();
        String url = ENDPOINT + "/" + messageId + "/read";
        mvc.perform(post(url))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithAnonymousUser
    void testSetMessageRead_unauthorized() throws Exception {
        String messageId = message1.getId().toString();
        String url = ENDPOINT + "/" + messageId + "/read";
        mvc.perform(post(url))
                .andExpect(status().isUnauthorized());
    }


    @Test
    @WithCustomSecurityContext(id = USER_ID_1)
    void testSetChatRead_ok() throws Exception {
        String chatId = chat1.getId().toString();
        String url = ENDPOINT + "/" + chatId + "/chat/read";
        mvc.perform(post(url))
                .andExpect(status().isCreated());
        List<Status> statusList = statusRepository.findAll();
        List<Status> filteredList = statusList.stream()
                .filter(status -> status.getMessage().getChat().getId().toString().equals(chatId)
                        && status.getUserId().toString().equals(USER_ID_1))
                .toList();
        boolean statusesExist = filteredList.stream()
                .allMatch(status -> status.getType().equals(StatusType.READ));
        assertThat(filteredList).isNotEmpty();
        assertThat(statusesExist).isTrue();
    }

    @Test
    @WithCustomSecurityContext(id = USER_ID_1)
    void testSetChatRead_ok_noMessagesFound() throws Exception {
        String chatId = chat2.getId().toString();
        String url = ENDPOINT + "/" + chatId + "/chat/read";
        mvc.perform(post(url))
                .andExpect(status().isCreated());
        List<Status> statusList = statusRepository.findAll();
        List<Status> filteredList = statusList.stream()
                .filter(status -> status.getMessage().getChat().getId().toString().equals(chatId)
                        && status.getUserId().toString().equals(USER_ID_1))
                .toList();
        assertThat(filteredList).isEmpty();
    }

    @Test
    @WithAnonymousUser
    void testSetChatRead_unauthorized() throws Exception {
        String chatId = chat2.getId().toString();
        String url = ENDPOINT + "/" + chatId + "/chat/read";
        mvc.perform(post(url))
                .andExpect(status().isUnauthorized());
    }


    private Chat createDirectChat() {
        Chat chat = TestChat.defaultBuilder().isDirect(true).build().toParent();
        return chatRepository.save(chat);
    }

    private void createMemberEvents(Chat chat, String... userIds) {
        List<MemberEvent> memberEventList = Arrays.stream(userIds)
                .map(id -> TestMemberEvent.defaultBuilder()
                        .chat(chat).userId(UUID.fromString(id)).build().toParent())
                .toList();
        memberEventRepository.saveAll(memberEventList);
    }

    private Message createMessage(Chat chat, String userId) {
        Message message = TestMessage.defaultBuilder()
                .chat(chat).userId(UUID.fromString(userId))
                .build().toParent();
        return messageRepository.save(message);
    }

    private void createStatuses(Message message, String... readUserIds) {
        Set<Status> statusList = Arrays.stream(readUserIds)
                .map(id -> TestStatus.defaultBuilder()
                        .message(message).userId(UUID.fromString(id)).build().toParent())
                .collect(Collectors.toSet());
        message.getStatuses().addAll(statusList);
        messageRepository.save(message);
    }
}
