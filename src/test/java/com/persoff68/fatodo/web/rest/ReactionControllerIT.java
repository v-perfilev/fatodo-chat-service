package com.persoff68.fatodo.web.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.persoff68.fatodo.FatodoChatServiceApplication;
import com.persoff68.fatodo.annotation.WithCustomSecurityContext;
import com.persoff68.fatodo.builder.TestChat;
import com.persoff68.fatodo.builder.TestMemberEvent;
import com.persoff68.fatodo.builder.TestMessage;
import com.persoff68.fatodo.builder.TestReaction;
import com.persoff68.fatodo.client.EventServiceClient;
import com.persoff68.fatodo.client.UserServiceClient;
import com.persoff68.fatodo.client.WsServiceClient;
import com.persoff68.fatodo.model.Chat;
import com.persoff68.fatodo.model.MemberEvent;
import com.persoff68.fatodo.model.Message;
import com.persoff68.fatodo.model.Reaction;
import com.persoff68.fatodo.model.constant.ReactionType;
import com.persoff68.fatodo.repository.ChatRepository;
import com.persoff68.fatodo.repository.MemberEventRepository;
import com.persoff68.fatodo.repository.MessageRepository;
import com.persoff68.fatodo.repository.ReactionRepository;
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
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = FatodoChatServiceApplication.class)
@AutoConfigureMockMvc
class ReactionControllerIT {
    private static final String ENDPOINT = "/api/reaction";

    private static final String USER_ID_1 = "3c300277-b5ea-48d1-80db-ead620cf5846";
    private static final String USER_ID_2 = "357a2a99-7b7e-4336-9cd7-18f2cf73fab9";
    private static final String USER_ID_3 = "71bae736-415b-474c-9865-29043cbc8d0c";

    private Message message1;
    private Message message2;
    private Message message3;
    private Message message4;

    @Autowired
    MockMvc mvc;
    @Autowired
    ChatRepository chatRepository;
    @Autowired
    MemberEventRepository memberEventRepository;
    @Autowired
    MessageRepository messageRepository;
    @Autowired
    ReactionRepository reactionRepository;
    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    UserServiceClient userServiceClient;
    @MockBean
    WsServiceClient wsServiceClient;
    @MockBean
    EventServiceClient eventServiceClient;

    @BeforeEach
    void setup() {
        Chat chat1 = createDirectChat();
        createMemberEvents(chat1, USER_ID_1, USER_ID_2);
        message1 = createMessage(chat1, USER_ID_2);
        message2 = createMessage(chat1, USER_ID_1);
        message3 = createMessage(chat1, USER_ID_2);
        createReaction(message3, USER_ID_1, ReactionType.DISLIKE);

        Chat chat2 = createDirectChat();
        createMemberEvents(chat2, USER_ID_2, USER_ID_3);
        message4 = createMessage(chat2, USER_ID_2);

        when(userServiceClient.doesIdExist(any())).thenReturn(true);
    }

    @AfterEach
    void cleanup() {
        chatRepository.deleteAll();
        memberEventRepository.deleteAll();
        messageRepository.deleteAll();
        reactionRepository.deleteAll();
    }

    @Test
    @WithCustomSecurityContext(id = USER_ID_1)
    void testSetLike_ok() throws Exception {
        String messageId = message1.getId().toString();
        String url = ENDPOINT + "/" + messageId + "/like";
        mvc.perform(post(url))
                .andExpect(status().isCreated());
        List<Reaction> reactionList = reactionRepository.findAll();
        boolean reactionExists = reactionList.stream()
                .anyMatch(status -> status.getMessage().getId().toString().equals(messageId)
                        && status.getUserId().toString().equals(USER_ID_1)
                        && status.getType().equals(ReactionType.LIKE));
        assertThat(reactionExists).isTrue();
    }

    @Test
    @WithCustomSecurityContext(id = USER_ID_1)
    void testSetLike_ok_wasDislike() throws Exception {
        String messageId = message3.getId().toString();
        String url = ENDPOINT + "/" + messageId + "/like";
        mvc.perform(post(url))
                .andExpect(status().isCreated());
        List<Reaction> reactionList = reactionRepository.findAll();
        boolean reactionExists = reactionList.stream()
                .anyMatch(status -> status.getMessage().getId().toString().equals(messageId)
                        && status.getUserId().toString().equals(USER_ID_1)
                        && status.getType().equals(ReactionType.LIKE));
        assertThat(reactionExists).isTrue();
    }

    @Test
    @WithCustomSecurityContext(id = USER_ID_1)
    void testSetLike_forbidden_ownMessage() throws Exception {
        String messageId = message2.getId().toString();
        String url = ENDPOINT + "/" + messageId + "/like";
        mvc.perform(post(url))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithCustomSecurityContext(id = USER_ID_1)
    void testSetLike_forbidden_noPermissions() throws Exception {
        String messageId = message4.getId().toString();
        String url = ENDPOINT + "/" + messageId + "/like";
        mvc.perform(post(url))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithCustomSecurityContext(id = USER_ID_1)
    void testSetLike_notFound() throws Exception {
        String messageId = UUID.randomUUID().toString();
        String url = ENDPOINT + "/" + messageId + "/like";
        mvc.perform(post(url))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithAnonymousUser
    void testSetLike_unauthorized() throws Exception {
        String messageId = message1.getId().toString();
        String url = ENDPOINT + "/" + messageId + "/like";
        mvc.perform(post(url))
                .andExpect(status().isUnauthorized());
    }


    @Test
    @WithCustomSecurityContext(id = USER_ID_1)
    void testSetDislike_ok() throws Exception {
        String messageId = message1.getId().toString();
        String url = ENDPOINT + "/" + messageId + "/dislike";
        mvc.perform(post(url))
                .andExpect(status().isCreated());
        List<Reaction> reactionList = reactionRepository.findAll();
        boolean reactionExists = reactionList.stream()
                .anyMatch(status -> status.getMessage().getId().toString().equals(messageId)
                        && status.getUserId().toString().equals(USER_ID_1)
                        && status.getType().equals(ReactionType.DISLIKE));
        assertThat(reactionExists).isTrue();
    }


    @Test
    @WithCustomSecurityContext(id = USER_ID_1)
    void testSetDislike_forbidden_ownMessage() throws Exception {
        String messageId = message2.getId().toString();
        String url = ENDPOINT + "/" + messageId + "/dislike";
        mvc.perform(post(url))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithCustomSecurityContext(id = USER_ID_1)
    void testSetDislike_forbidden_noPermissions() throws Exception {
        String messageId = message4.getId().toString();
        String url = ENDPOINT + "/" + messageId + "/dislike";
        mvc.perform(post(url))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithCustomSecurityContext(id = USER_ID_1)
    void testSetDislike_notFound() throws Exception {
        String messageId = UUID.randomUUID().toString();
        String url = ENDPOINT + "/" + messageId + "/dislike";
        mvc.perform(post(url))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithAnonymousUser
    void testSetDislike_unauthorized() throws Exception {
        String messageId = message1.getId().toString();
        String url = ENDPOINT + "/" + messageId + "/dislike";
        mvc.perform(post(url))
                .andExpect(status().isUnauthorized());
    }


    @Test
    @WithCustomSecurityContext(id = USER_ID_1)
    void testSetNone_ok() throws Exception {
        String messageId = message3.getId().toString();
        String url = ENDPOINT + "/" + messageId;
        mvc.perform(delete(url))
                .andExpect(status().isCreated());
        List<Reaction> reactionList = reactionRepository.findAll();
        boolean reactionExists = reactionList.stream()
                .anyMatch(status -> status.getMessage().getId().toString().equals(messageId)
                        && status.getUserId().toString().equals(USER_ID_1));
        assertThat(reactionExists).isFalse();
    }


    @Test
    @WithCustomSecurityContext(id = USER_ID_1)
    void testSetNone_forbidden_ownMessage() throws Exception {
        String messageId = message2.getId().toString();
        String url = ENDPOINT + "/" + messageId;
        mvc.perform(delete(url))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithCustomSecurityContext(id = USER_ID_1)
    void testSetNone_forbidden_noPermissions() throws Exception {
        String messageId = message4.getId().toString();
        String url = ENDPOINT + "/" + messageId;
        mvc.perform(delete(url))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithCustomSecurityContext(id = USER_ID_1)
    void testSetNone_notFound() throws Exception {
        String messageId = UUID.randomUUID().toString();
        String url = ENDPOINT + "/" + messageId;
        mvc.perform(delete(url))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithAnonymousUser
    void testSetNone_unauthorized() throws Exception {
        String messageId = message1.getId().toString();
        String url = ENDPOINT + "/" + messageId;
        mvc.perform(delete(url))
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

    private void createReaction(Message message, String userId, ReactionType type) {
        Reaction reaction = TestReaction.defaultBuilder()
                .message(message).userId(UUID.fromString(userId)).type(type).build().toParent();
        message.getReactions().add(reaction);
        messageRepository.save(message);
    }

}
