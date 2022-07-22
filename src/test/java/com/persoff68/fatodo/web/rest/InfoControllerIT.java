package com.persoff68.fatodo.web.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.persoff68.fatodo.FatodoChatServiceApplication;
import com.persoff68.fatodo.annotation.WithCustomSecurityContext;
import com.persoff68.fatodo.builder.TestChat;
import com.persoff68.fatodo.builder.TestMessage;
import com.persoff68.fatodo.client.EventServiceClient;
import com.persoff68.fatodo.client.UserServiceClient;
import com.persoff68.fatodo.client.WsServiceClient;
import com.persoff68.fatodo.model.Chat;
import com.persoff68.fatodo.model.MemberEvent;
import com.persoff68.fatodo.model.Message;
import com.persoff68.fatodo.model.constant.MemberEventType;
import com.persoff68.fatodo.model.dto.ChatInfoDTO;
import com.persoff68.fatodo.model.dto.MessageInfoDTO;
import com.persoff68.fatodo.repository.ChatRepository;
import com.persoff68.fatodo.repository.MemberEventRepository;
import com.persoff68.fatodo.repository.MessageRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest(classes = FatodoChatServiceApplication.class)
@AutoConfigureMockMvc
class InfoControllerIT {
    private static final String ENDPOINT = "/api/info";

    private static final String USER_ID_1 = "3c300277-b5ea-48d1-80db-ead620cf5846";

    private Chat chat1;
    private Chat chat2;
    private Message message1;
    private Message message2;

    @Autowired
    MockMvc mvc;
    @Autowired
    ChatRepository chatRepository;
    @Autowired
    MessageRepository messageRepository;
    @Autowired
    MemberEventRepository memberEventRepository;
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
        chat1 = createChat(USER_ID_1);
        message1 = createMessage(chat1, USER_ID_1);

        chat2 = createChat(UUID.randomUUID().toString());
        message2 = createMessage(chat2, USER_ID_1);
    }

    @AfterEach
    void cleanup() {
        chatRepository.deleteAll();
        messageRepository.deleteAll();
        memberEventRepository.deleteAll();
    }


    @Test
    @WithCustomSecurityContext(id = USER_ID_1)
    void getAllChatInfoByIds_ok() throws Exception {
        String url = ENDPOINT + "/chats";
        List<UUID> chatIdList = List.of(chat1.getId(), chat2.getId());
        String requestBody = objectMapper.writeValueAsString(chatIdList);
        ResultActions resultActions = mvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isOk());
        String resultString = resultActions.andReturn().getResponse().getContentAsString();
        CollectionType listType = objectMapper.getTypeFactory().constructCollectionType(List.class, ChatInfoDTO.class);
        List<ChatInfoDTO> resultDto = objectMapper.readValue(resultString, listType);
        assertThat(resultDto).hasSize(1);
        assertThat(resultDto.get(0).getId()).isEqualTo(chat1.getId());
        assertThat(resultDto.get(0).getMembers()).isNotNull().isNotEmpty();
    }

    @Test
    @WithAnonymousUser
    void getAllChatInfoByIds_unauthorized() throws Exception {
        String url = ENDPOINT + "/chats";
        List<UUID> chatIdList = List.of(chat1.getId(), chat2.getId());
        String requestBody = objectMapper.writeValueAsString(chatIdList);
        mvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isUnauthorized());
    }


    @Test
    @WithCustomSecurityContext(id = USER_ID_1)
    void getAllMessageInfoByIds_ok() throws Exception {
        String url = ENDPOINT + "/messages";
        List<UUID> chatIdList = List.of(message1.getId(), message2.getId());
        String requestBody = objectMapper.writeValueAsString(chatIdList);
        ResultActions resultActions = mvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isOk());
        String resultString = resultActions.andReturn().getResponse().getContentAsString();
        CollectionType listType = objectMapper.getTypeFactory().constructCollectionType(List.class,
                MessageInfoDTO.class);
        List<MessageInfoDTO> resultDto = objectMapper.readValue(resultString, listType);
        assertThat(resultDto).hasSize(1);
        assertThat(resultDto.get(0).getId()).isEqualTo(message1.getId());
    }

    @Test
    @WithAnonymousUser
    void getAllMessageInfoByIds_unauthorized() throws Exception {
        String url = ENDPOINT + "/messages";
        List<UUID> chatIdList = List.of(message1.getId(), message2.getId());
        String requestBody = objectMapper.writeValueAsString(chatIdList);
        mvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isUnauthorized());
    }


    private Chat createChat(String... userIds) {
        Chat chat = TestChat.defaultBuilder().isDirect(false).build().toParent();
        chatRepository.saveAndFlush(chat);
        createAddMemberEvents(chat, userIds);
        return chat;
    }

    private void createAddMemberEvents(Chat chat, String... userIds) {
        List<MemberEvent> messageList = Arrays.stream(userIds)
                .map(UUID::fromString)
                .map(userId -> new MemberEvent(chat, userId, MemberEventType.ADD_MEMBER))
                .toList();
        memberEventRepository.saveAll(messageList);
        memberEventRepository.flush();
    }

    private Message createMessage(Chat chat, String userId) {
        Message message = TestMessage.defaultBuilder()
                .chat(chat).userId(UUID.fromString(userId)).build().toParent();
        return messageRepository.saveAndFlush(message);
    }

}
