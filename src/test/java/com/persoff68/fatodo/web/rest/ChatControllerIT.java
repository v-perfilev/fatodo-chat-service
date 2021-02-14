package com.persoff68.fatodo.web.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.persoff68.fatodo.FatodoMessageServiceApplication;
import com.persoff68.fatodo.annotation.WithCustomSecurityContext;
import com.persoff68.fatodo.builder.TestChat;
import com.persoff68.fatodo.client.UserServiceClient;
import com.persoff68.fatodo.model.Chat;
import com.persoff68.fatodo.model.dto.ChatDTO;
import com.persoff68.fatodo.repository.ChatRepository;
import com.persoff68.fatodo.repository.MemberEventRepository;
import com.persoff68.fatodo.service.MemberEventService;
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
import java.util.UUID;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = FatodoMessageServiceApplication.class)
public class ChatControllerIT {
    private static final String ENDPOINT = "/api/chat";

    private static final String USER_ID_1 = "3c300277-b5ea-48d1-80db-ead620cf5846";
    private static final String USER_ID_2 = "357a2a99-7b7e-4336-9cd7-18f2cf73fab9";
    private static final String USER_ID_3 = "a762e074-0c26-4a3e-9495-44ccb2baf85c";

    private Chat chat1;
    private Chat chat2;

    @Autowired
    WebApplicationContext context;
    @Autowired
    ChatRepository chatRepository;
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
        memberEventRepository.deleteAll();

        for (int i = 0; i < 20; i++) {
            chat1 = createChat(false);
            createMemberEvents(chat1, USER_ID_1, USER_ID_2);
        }

        chat2 = createChat(false);
        createMemberEvents(chat2, USER_ID_2, USER_ID_3);

        Chat directChat = createChat(true);
        createMemberEvents(directChat, USER_ID_1, USER_ID_2);
    }


    @Test
    @WithCustomSecurityContext(id = USER_ID_1)
    void testGetAllPageable_ok_withoutParams() throws Exception {
        ResultActions resultActions = mvc.perform(get(ENDPOINT))
                .andExpect(status().isOk());
        String resultString = resultActions.andReturn().getResponse().getContentAsString();
        CollectionType listType = objectMapper.getTypeFactory().constructCollectionType(List.class, ChatDTO.class);
        List<ChatDTO> resultDTOList = objectMapper.readValue(resultString, listType);
        assertThat(resultDTOList.size()).isEqualTo(ChatController.DEFAULT_SIZE);
    }

    @Test
    @WithCustomSecurityContext(id = USER_ID_1)
    void testGetAllPageable_ok_withParams() throws Exception {
        String url = ENDPOINT + "?offset=15&size=10";
        ResultActions resultActions = mvc.perform(get(url))
                .andExpect(status().isOk());
        String resultString = resultActions.andReturn().getResponse().getContentAsString();
        CollectionType listType = objectMapper.getTypeFactory().constructCollectionType(List.class, ChatDTO.class);
        List<ChatDTO> resultDTOList = objectMapper.readValue(resultString, listType);
        assertThat(resultDTOList.size()).isEqualTo(6);
    }

    @Test
    @WithAnonymousUser
    void testGetAllPageable_unauthorized() throws Exception {
        mvc.perform(get(ENDPOINT))
                .andExpect(status().isUnauthorized());
    }


    @Test
    @WithCustomSecurityContext(id = USER_ID_1)
    void testGetById_ok() throws Exception {
        String url = ENDPOINT + "/" + chat1.getId().toString();
        ResultActions resultActions = mvc.perform(get(url))
                .andExpect(status().isOk());
        String resultString = resultActions.andReturn().getResponse().getContentAsString();
        ChatDTO resultDTO = objectMapper.readValue(resultString, ChatDTO.class);
        assertThat(resultDTO.getId()).isEqualTo(chat1.getId());
    }

    @Test
    @WithCustomSecurityContext(id = USER_ID_1)
    void testGetById_badRequest_noPermissions() throws Exception {
        String url = ENDPOINT + "/" + chat2.getId().toString();
        mvc.perform(get(url))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithCustomSecurityContext(id = USER_ID_1)
    void testGetById_notFound() throws Exception {
        String url = ENDPOINT + "/" + UUID.randomUUID().toString();
        mvc.perform(get(url))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithAnonymousUser
    void testGetById_unauthorized() throws Exception {
        String url = ENDPOINT + "/" + UUID.randomUUID().toString();
        mvc.perform(get(url))
                .andExpect(status().isUnauthorized());
    }


    @Test
    @WithCustomSecurityContext(id = USER_ID_1)
    void testCreateDirect_ok() throws Exception {
        String url = ENDPOINT + "/create-direct";
        UUID userId = UUID.fromString(USER_ID_3);
        String requestBody = objectMapper.writeValueAsString(userId);
        ResultActions resultActions = mvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isCreated());
        String resultString = resultActions.andReturn().getResponse().getContentAsString();
        ChatDTO resultDTO = objectMapper.readValue(resultString, ChatDTO.class);
        assertThat(resultDTO.getMembers()).contains(UUID.fromString(USER_ID_1), UUID.fromString(USER_ID_3));
        assertThat(resultDTO.isDirect()).isTrue();
    }

    @Test
    @WithCustomSecurityContext(id = USER_ID_1)
    void testCreateDirect_badRequest_alreadyExists() throws Exception {
        String url = ENDPOINT + "/create-direct";
        UUID userId = UUID.fromString(USER_ID_2);
        String requestBody = objectMapper.writeValueAsString(userId);
        mvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithCustomSecurityContext(id = USER_ID_1)
    void testCreateDirect_notFound() throws Exception {
        when(userServiceClient.doesIdExist(any())).thenReturn(false);
        String url = ENDPOINT + "/create-direct";
        UUID userId = UUID.randomUUID();
        String requestBody = objectMapper.writeValueAsString(userId);
        mvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithAnonymousUser
    void testCreateDirect_unauthorized() throws Exception {
        String url = ENDPOINT + "/create-direct";
        UUID userId = UUID.fromString(USER_ID_2);
        String requestBody = objectMapper.writeValueAsString(userId);
        mvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isUnauthorized());
    }


    @Test
    @WithCustomSecurityContext(id = USER_ID_1)
    void testCreateIndirect_ok() throws Exception {
        String url = ENDPOINT + "/create-indirect";
        List<UUID> userIdList = List.of(UUID.fromString(USER_ID_2), UUID.fromString(USER_ID_3));
        String requestBody = objectMapper.writeValueAsString(userIdList);
        ResultActions resultActions = mvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isCreated());
        String resultString = resultActions.andReturn().getResponse().getContentAsString();
        ChatDTO resultDTO = objectMapper.readValue(resultString, ChatDTO.class);
        assertThat(resultDTO.getMembers()).contains(
                UUID.fromString(USER_ID_1),
                UUID.fromString(USER_ID_2),
                UUID.fromString(USER_ID_3));
        assertThat(resultDTO.isDirect()).isFalse();
    }


    @Test
    @WithCustomSecurityContext(id = USER_ID_1)
    void testCreateIndirect_notFound() throws Exception {
        when(userServiceClient.doesIdExist(any())).thenReturn(false);
        String url = ENDPOINT + "/create-indirect";
        List<UUID> userIdList = List.of(UUID.fromString(USER_ID_2), UUID.fromString(USER_ID_3));
        String requestBody = objectMapper.writeValueAsString(userIdList);
        mvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isNotFound());
    }


    @Test
    @WithAnonymousUser
    void testCreateIndirect_unauthorized() throws Exception {
        String url = ENDPOINT + "/create-indirect";
        List<UUID> userIdList = List.of(UUID.fromString(USER_ID_2), UUID.fromString(USER_ID_3));
        String requestBody = objectMapper.writeValueAsString(userIdList);
        mvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isUnauthorized());
    }


    @Test
    @WithCustomSecurityContext(id = USER_ID_1)
    void testRename_ok() throws Exception {
        String url = ENDPOINT + "/rename/" + chat1.getId().toString();
        String requestBody = "test_name";
        ResultActions resultActions = mvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isOk());
        String resultString = resultActions.andReturn().getResponse().getContentAsString();
        ChatDTO resultDTO = objectMapper.readValue(resultString, ChatDTO.class);
        assertThat(resultDTO.getId()).isEqualTo(chat1.getId());
        assertThat(resultDTO.getTitle()).isEqualTo(requestBody);
    }


    @Test
    @WithCustomSecurityContext(id = USER_ID_1)
    void testRename_badRequest_noPermissions() throws Exception {
        String url = ENDPOINT + "/rename/" + chat2.getId().toString();
        String requestBody = "test_name";
        mvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithCustomSecurityContext(id = USER_ID_1)
    void testRename_notFound() throws Exception {
        String url = ENDPOINT + "/rename/" + UUID.randomUUID().toString();
        String requestBody = "test_name";
        mvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithAnonymousUser
    void testRename_unauthorized() throws Exception {
        String url = ENDPOINT + "/rename/" + chat1.getId().toString();
        String requestBody = "test_name";
        mvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isUnauthorized());
    }


    private Chat createChat(boolean isDirect) {
        Chat chat = TestChat.defaultBuilder().isDirect(isDirect).build().toParent();
        return chatRepository.save(chat);
    }

    private void createMemberEvents(Chat chat, String... userIds) {
        List<UUID> userIdList = Arrays.stream(userIds)
                .map(UUID::fromString)
                .collect(Collectors.toList());
        memberEventService.addUsersUnsafe(chat.getId(), userIdList);
    }

}
