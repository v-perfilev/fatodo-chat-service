package com.persoff68.fatodo.web.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.persoff68.fatodo.FatodoChatServiceApplication;
import com.persoff68.fatodo.annotation.WithCustomSecurityContext;
import com.persoff68.fatodo.builder.TestChat;
import com.persoff68.fatodo.builder.TestMessage;
import com.persoff68.fatodo.builder.TestMessageVM;
import com.persoff68.fatodo.client.UserServiceClient;
import com.persoff68.fatodo.client.WsServiceClient;
import com.persoff68.fatodo.model.Chat;
import com.persoff68.fatodo.model.MemberEvent;
import com.persoff68.fatodo.model.Message;
import com.persoff68.fatodo.model.constant.MemberEventType;
import com.persoff68.fatodo.model.dto.MessageDTO;
import com.persoff68.fatodo.repository.ChatRepository;
import com.persoff68.fatodo.repository.MemberEventRepository;
import com.persoff68.fatodo.repository.MessageRepository;
import com.persoff68.fatodo.web.rest.vm.MessageVM;
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
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest(classes = FatodoChatServiceApplication.class)
@AutoConfigureMockMvc
public class MessageControllerIT {
    private static final String ENDPOINT = "/api/messages";

    private static final String USER_ID_1 = "3c300277-b5ea-48d1-80db-ead620cf5846";
    private static final String USER_ID_2 = "357a2a99-7b7e-4336-9cd7-18f2cf73fab9";
    private static final String USER_ID_3 = "a762e074-0c26-4a3e-9495-44ccb2baf85c";

    private Chat chat1;
    private Chat chat2;
    private Message message1;
    private Message message2;
    private Message message3;

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

    @BeforeEach
    public void setup() throws InterruptedException {
        when(userServiceClient.doesIdExist(any())).thenReturn(true);
        when(userServiceClient.doIdsExist(any())).thenReturn(true);
        doNothing().when(wsServiceClient).sendChatLastMessageEvent(any());
        doNothing().when(wsServiceClient).sendChatLastMessageUpdateEvent(any());
        doNothing().when(wsServiceClient).sendMessageNewEvent(any());
        doNothing().when(wsServiceClient).sendMessageUpdateEvent(any());

        chatRepository.deleteAll();
        messageRepository.deleteAll();
        memberEventRepository.deleteAll();

        chat1 = createChat(USER_ID_1, USER_ID_2);
        for (int i = 0; i < 10; i++) {
            message1 = createMessage(chat1, USER_ID_1);
            message2 = createMessage(chat1, USER_ID_2);
        }

        chat2 = createChat(USER_ID_2, USER_ID_3);
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
    void testGetAllByUserIdPageable_forbidden() throws Exception {
        String url = ENDPOINT + "/" + chat2.getId().toString();
        mvc.perform(get(url))
                .andExpect(status().isForbidden());
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
        String url = ENDPOINT + "/direct/" + UUID.randomUUID();
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
        String url = ENDPOINT + "/direct/" + UUID.randomUUID();
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
    void testSend_forbidden() throws Exception {
        String url = ENDPOINT + "/" + chat2.getId().toString();
        MessageVM vm = TestMessageVM.defaultBuilder().build().toParent();
        String requestBody = objectMapper.writeValueAsString(vm);
        mvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithCustomSecurityContext(id = USER_ID_1)
    void testSend_notFound() throws Exception {
        String url = ENDPOINT + "/" + UUID.randomUUID();
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
    void testEdit_forbidden_notOwner() throws Exception {
        String url = ENDPOINT + "/" + message2.getId().toString();
        MessageVM vm = TestMessageVM.defaultBuilder().text("new_text").build().toParent();
        String requestBody = objectMapper.writeValueAsString(vm);
        mvc.perform(put(url)
                        .contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithCustomSecurityContext(id = USER_ID_1)
    void testEdit_forbidden_wrongChat() throws Exception {
        String url = ENDPOINT + "/" + message3.getId().toString();
        MessageVM vm = TestMessageVM.defaultBuilder().text("new_text").build().toParent();
        String requestBody = objectMapper.writeValueAsString(vm);
        mvc.perform(put(url)
                        .contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithCustomSecurityContext(id = USER_ID_1)
    void testEdit_notFound() throws Exception {
        String url = ENDPOINT + "/" + UUID.randomUUID();
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
    }

    @Test
    @WithCustomSecurityContext(id = USER_ID_1)
    void testDelete_forbidden_notOwner() throws Exception {
        String url = ENDPOINT + "/" + message2.getId().toString();
        mvc.perform(delete(url))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithCustomSecurityContext(id = USER_ID_1)
    void testDelete_forbidden_wrongChat() throws Exception {
        String url = ENDPOINT + "/" + message3.getId().toString();
        mvc.perform(delete(url))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithCustomSecurityContext(id = USER_ID_1)
    void testDelete_notFound() throws Exception {
        String url = ENDPOINT + "/" + UUID.randomUUID();
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
                .collect(Collectors.toList());
        memberEventRepository.saveAll(messageList);
        memberEventRepository.flush();
    }

    private Message createMessage(Chat chat, String userId) {
        Message message = TestMessage.defaultBuilder()
                .chat(chat).userId(UUID.fromString(userId)).build().toParent();
        return messageRepository.saveAndFlush(message);
    }

}
