package com.persoff68.fatodo.web.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.persoff68.fatodo.FatodoMessageServiceApplication;
import com.persoff68.fatodo.annotation.WithCustomSecurityContext;
import com.persoff68.fatodo.builder.TestChat;
import com.persoff68.fatodo.builder.TestMessage;
import com.persoff68.fatodo.builder.TestMessageVM;
import com.persoff68.fatodo.client.UserServiceClient;
import com.persoff68.fatodo.model.Chat;
import com.persoff68.fatodo.model.Message;
import com.persoff68.fatodo.model.dto.MessageDTO;
import com.persoff68.fatodo.repository.ChatRepository;
import com.persoff68.fatodo.repository.MemberEventRepository;
import com.persoff68.fatodo.repository.MessageRepository;
import com.persoff68.fatodo.service.MemberEventService;
import com.persoff68.fatodo.web.rest.vm.MessageVM;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest(classes = FatodoMessageServiceApplication.class)
public class MessageControllerIT {
    private static final String ENDPOINT = "/api/message";

    private static final String USER_ID_1 = "3c300277-b5ea-48d1-80db-ead620cf5846";
    private static final String USER_ID_2 = "357a2a99-7b7e-4336-9cd7-18f2cf73fab9";
    private static final String USER_ID_3 = "a762e074-0c26-4a3e-9495-44ccb2baf85c";

    private Chat chat1;
    private Chat chat2;
    private Message message1;
    private Message message2;
    private Message message3;

    @Autowired
    WebApplicationContext context;
    @Autowired
    ChatRepository chatRepository;
    @Autowired
    MessageRepository messageRepository;
    @Autowired
    MemberEventRepository memberEventRepository;
    @Autowired
    MemberEventService memberEventService;
    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    UserServiceClient userServiceClient;

    MockMvc mvc;

    @BeforeEach
    public void setup() {
        mvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();

        when(userServiceClient.doesIdExist(any())).thenReturn(true);

        chatRepository.deleteAll();
        messageRepository.deleteAll();
        memberEventRepository.deleteAll();

        chat1 = createChat();
        createMemberEvents(chat1, USER_ID_1, USER_ID_2);
        for (int i = 0; i < 10; i++) {
            message1 = createMessage(chat1, USER_ID_1);
            message2 = createMessage(chat1, USER_ID_2);
        }

        chat2 = createChat();
        createMemberEvents(chat2, USER_ID_2, USER_ID_3);
        for (int i = 0; i < 10; i++) {
            message3 = createMessage(chat2, USER_ID_2);
        }
    }


    @Test
    @WithCustomSecurityContext(id = USER_ID_1)
    void testGetAllByUserIdPageable_ok_withoutParams() throws Exception {
        String url = ENDPOINT + "/" + chat1.getId().toString();
        ResultActions resultActions = mvc.perform(get(url))
                .andExpect(status().isOk());
        String resultString = resultActions.andReturn().getResponse().getContentAsString();
        CollectionType listType = objectMapper.getTypeFactory().constructCollectionType(List.class, MessageDTO.class);
        List<MessageDTO> resultDTOList = objectMapper.readValue(resultString, listType);
        assertThat(resultDTOList.size()).isEqualTo(20);
    }

    @Test
    @WithCustomSecurityContext(id = USER_ID_1)
    void testGetAllByUserIdPageable_ok_witParams() throws Exception {
        String url = ENDPOINT + "/" + chat1.getId().toString() + "?offset=5&size=10";
        ResultActions resultActions = mvc.perform(get(url))
                .andExpect(status().isOk());
        String resultString = resultActions.andReturn().getResponse().getContentAsString();
        CollectionType listType = objectMapper.getTypeFactory().constructCollectionType(List.class, MessageDTO.class);
        List<MessageDTO> resultDTOList = objectMapper.readValue(resultString, listType);
        assertThat(resultDTOList.size()).isEqualTo(10);
    }

    @Test
    @WithCustomSecurityContext(id = USER_ID_1)
    void testGetAllByUserIdPageable_badRequest_noPermissions() throws Exception {
        String url = ENDPOINT + "/" + chat2.getId().toString();
        mvc.perform(get(url))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithAnonymousUser
    void testGetAllByUserIdPageable_unauthorized() throws Exception {
        String url = ENDPOINT + "/" + chat1.getId().toString();
        mvc.perform(get(url))
                .andExpect(status().isUnauthorized());
    }


    @Test
    @WithCustomSecurityContext(id = USER_ID_1)
    void testSendDirect_ok() throws Exception {
        String url = ENDPOINT + "/direct/" + USER_ID_3;
        MessageVM vm = TestMessageVM.defaultBuilder().build().toParent();
        String requestBody = objectMapper.writeValueAsString(vm);
        ResultActions resultActions = mvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isCreated());
        String resultString = resultActions.andReturn().getResponse().getContentAsString();
        MessageDTO resultDTO = objectMapper.readValue(resultString, MessageDTO.class);
        assertThat(resultDTO.getUserId()).isEqualTo(UUID.fromString(USER_ID_1));
        assertThat(resultDTO.getText()).isEqualTo(vm.getText());
    }

    @Test
    @WithCustomSecurityContext(id = USER_ID_1)
    void testSendDirect_notFound() throws Exception {
        when(userServiceClient.doesIdExist(any())).thenReturn(false);
        String url = ENDPOINT + "/direct/" + UUID.randomUUID().toString();
        MessageVM vm = TestMessageVM.defaultBuilder().build().toParent();
        String requestBody = objectMapper.writeValueAsString(vm);
        mvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithAnonymousUser
    void testSendDirect_unauthorized() throws Exception {
        when(userServiceClient.doesIdExist(any())).thenReturn(false);
        String url = ENDPOINT + "/direct/" + UUID.randomUUID().toString();
        MessageVM vm = TestMessageVM.defaultBuilder().build().toParent();
        String requestBody = objectMapper.writeValueAsString(vm);
        mvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isUnauthorized());
    }


    @Test
    @WithCustomSecurityContext(id = USER_ID_1)
    void testSend_ok() throws Exception {
        String url = ENDPOINT + "/" + chat1.getId().toString();
        MessageVM vm = TestMessageVM.defaultBuilder().build().toParent();
        String requestBody = objectMapper.writeValueAsString(vm);
        ResultActions resultActions = mvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isCreated());
        String resultString = resultActions.andReturn().getResponse().getContentAsString();
        MessageDTO resultDTO = objectMapper.readValue(resultString, MessageDTO.class);
        assertThat(resultDTO.getUserId()).isEqualTo(UUID.fromString(USER_ID_1));
        assertThat(resultDTO.getText()).isEqualTo(vm.getText());
    }

    @Test
    @WithCustomSecurityContext(id = USER_ID_1)
    void testSend_badRequest_noPermissions() throws Exception {
        String url = ENDPOINT + "/" + chat2.getId().toString();
        MessageVM vm = TestMessageVM.defaultBuilder().build().toParent();
        String requestBody = objectMapper.writeValueAsString(vm);
        mvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithCustomSecurityContext(id = USER_ID_1)
    void testSend_notFound() throws Exception {
        String url = ENDPOINT + "/" + UUID.randomUUID().toString();
        MessageVM vm = TestMessageVM.defaultBuilder().build().toParent();
        String requestBody = objectMapper.writeValueAsString(vm);
        mvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithAnonymousUser
    void testSend_unauthorized() throws Exception {
        String url = ENDPOINT + "/" + chat1.getId().toString();
        MessageVM vm = TestMessageVM.defaultBuilder().build().toParent();
        String requestBody = objectMapper.writeValueAsString(vm);
        mvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isUnauthorized());
    }


    @Test
    @WithCustomSecurityContext(id = USER_ID_1)
    void testEdit_ok() throws Exception {
        String url = ENDPOINT + "/" + message1.getId().toString();
        MessageVM vm = TestMessageVM.defaultBuilder().text("new_text").build().toParent();
        String requestBody = objectMapper.writeValueAsString(vm);
        ResultActions resultActions = mvc.perform(put(url)
                .contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isOk());
        String resultString = resultActions.andReturn().getResponse().getContentAsString();
        MessageDTO resultDTO = objectMapper.readValue(resultString, MessageDTO.class);
        assertThat(resultDTO.getUserId()).isEqualTo(UUID.fromString(USER_ID_1));
        assertThat(resultDTO.getText()).isEqualTo(vm.getText());
    }

    @Test
    @WithCustomSecurityContext(id = USER_ID_1)
    void testEdit_badRequest_notOwner() throws Exception {
        String url = ENDPOINT + "/" + message2.getId().toString();
        MessageVM vm = TestMessageVM.defaultBuilder().text("new_text").build().toParent();
        String requestBody = objectMapper.writeValueAsString(vm);
        mvc.perform(put(url)
                .contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithCustomSecurityContext(id = USER_ID_1)
    void testEdit_badRequest_wrongChat() throws Exception {
        String url = ENDPOINT + "/" + message3.getId().toString();
        MessageVM vm = TestMessageVM.defaultBuilder().text("new_text").build().toParent();
        String requestBody = objectMapper.writeValueAsString(vm);
        mvc.perform(put(url)
                .contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithCustomSecurityContext(id = USER_ID_1)
    void testEdit_notFound() throws Exception {
        String url = ENDPOINT + "/" + UUID.randomUUID().toString();
        MessageVM vm = TestMessageVM.defaultBuilder().text("new_text").build().toParent();
        String requestBody = objectMapper.writeValueAsString(vm);
        mvc.perform(put(url)
                .contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithAnonymousUser
    void testEdit_unauthorized() throws Exception {
        String url = ENDPOINT + "/" + message1.getId().toString();
        MessageVM vm = TestMessageVM.defaultBuilder().text("new_text").build().toParent();
        String requestBody = objectMapper.writeValueAsString(vm);
        mvc.perform(put(url)
                .contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isUnauthorized());
    }


    @Test
    @WithCustomSecurityContext(id = USER_ID_1)
    void testDelete_ok() throws Exception {
        String url = ENDPOINT + "/" + message1.getId().toString();
        mvc.perform(delete(url))
                .andExpect(status().isOk());
        Optional<Message> messageOptional = messageRepository.findById(message1.getId());
        assertThat(messageOptional).isEmpty();
    }

    @Test
    @WithCustomSecurityContext(id = USER_ID_1)
    void testDelete_badRequest_notOwner() throws Exception {
        String url = ENDPOINT + "/" + message2.getId().toString();
        mvc.perform(delete(url))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithCustomSecurityContext(id = USER_ID_1)
    void testDelete_badRequest_wrongChat() throws Exception {
        String url = ENDPOINT + "/" + message3.getId().toString();
        mvc.perform(delete(url))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithCustomSecurityContext(id = USER_ID_1)
    void testDelete_notFound() throws Exception {
        String url = ENDPOINT + "/" + UUID.randomUUID().toString();
        mvc.perform(delete(url))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithAnonymousUser
    void testDelete_unauthorized() throws Exception {
        String url = ENDPOINT + "/" + message1.getId().toString();
        mvc.perform(delete(url))
                .andExpect(status().isUnauthorized());
    }


    private Chat createChat() {
        Chat chat = TestChat.defaultBuilder().isDirect(false).build().toParent();
        return chatRepository.saveAndFlush(chat);
    }

    private void createMemberEvents(Chat chat, String... userIds) {
        List<UUID> userIdList = Arrays.stream(userIds)
                .map(UUID::fromString)
                .collect(Collectors.toList());
        memberEventService.addUsersUnsafe(chat.getId(), userIdList);
    }

    private Message createMessage(Chat chat, String userId) {
        Message message = TestMessage.defaultBuilder()
                .chat(chat).userId(UUID.fromString(userId)).build().toParent();
        return messageRepository.saveAndFlush(message);
    }


}
