package com.persoff68.fatodo.web.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.persoff68.fatodo.FatodoChatServiceApplication;
import com.persoff68.fatodo.annotation.WithCustomSecurityContext;
import com.persoff68.fatodo.builder.TestChat;
import com.persoff68.fatodo.client.UserServiceClient;
import com.persoff68.fatodo.client.WsServiceClient;
import com.persoff68.fatodo.model.Chat;
import com.persoff68.fatodo.model.MemberEvent;
import com.persoff68.fatodo.model.Message;
import com.persoff68.fatodo.model.constant.MemberEventType;
import com.persoff68.fatodo.model.dto.ChatDTO;
import com.persoff68.fatodo.repository.ChatRepository;
import com.persoff68.fatodo.repository.MemberEventRepository;
import com.persoff68.fatodo.repository.MessageRepository;
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
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = FatodoChatServiceApplication.class)
@AutoConfigureMockMvc
public class ChatControllerIT {
    private static final String ENDPOINT = "/api/chats";

    private static final String USER_ID_1 = "3c300277-b5ea-48d1-80db-ead620cf5846";
    private static final String USER_ID_2 = "357a2a99-7b7e-4336-9cd7-18f2cf73fab9";
    private static final String USER_ID_3 = "a762e074-0c26-4a3e-9495-44ccb2baf85c";
    private static final String USER_ID_4 = "e3526697-ce05-4cad-b289-97b8755169c1";

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

    private Chat chat1;
    private Chat chat2;

    @BeforeEach
    public void setup() {
        when(userServiceClient.doesIdExist(any())).thenReturn(true);
        when(userServiceClient.getAllIdsByUsernamePart(any())).thenReturn(Collections.emptyList());
        doNothing().when(wsServiceClient).sendChatNewEvent(any());
        doNothing().when(wsServiceClient).sendChatUpdateEvent(any());

        chatRepository.deleteAll();
        memberEventRepository.deleteAll();

        chat1 = createChat("test_chat", false, USER_ID_1, USER_ID_2);
        chat2 = createChat(null, false, USER_ID_2, USER_ID_3);
        createChat(null, true, USER_ID_1, USER_ID_4);
    }

    @Test
    @WithCustomSecurityContext(id = USER_ID_1)
    void testGetAllPageable_ok_withoutParams() throws Exception {
        ResultActions resultActions = mvc.perform(get(ENDPOINT))
                .andExpect(status().isOk());
        String resultString = resultActions.andReturn().getResponse().getContentAsString();
        CollectionType listType = objectMapper.getTypeFactory().constructCollectionType(List.class, ChatDTO.class);
        List<ChatDTO> resultDTOList = objectMapper.readValue(resultString, listType);
        assertThat(resultDTOList.size()).isEqualTo(2);
    }

    @Test
    @WithCustomSecurityContext(id = USER_ID_1)
    void testGetAllPageable_ok_withParams() throws Exception {
        String url = ENDPOINT + "?offset=1&size=10";
        ResultActions resultActions = mvc.perform(get(url))
                .andExpect(status().isOk());
        String resultString = resultActions.andReturn().getResponse().getContentAsString();
        CollectionType listType = objectMapper.getTypeFactory().constructCollectionType(List.class, ChatDTO.class);
        List<ChatDTO> resultDTOList = objectMapper.readValue(resultString, listType);
        assertThat(resultDTOList.size()).isEqualTo(1);
    }

    @Test
    @WithAnonymousUser
    void testGetAllPageable_unauthorized() throws Exception {
        mvc.perform(get(ENDPOINT))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithCustomSecurityContext(id = USER_ID_1)
    void testGetFiltered_ok_byTitle() throws Exception {
        String url = ENDPOINT + "/filtered/test";
        ResultActions resultActions = mvc.perform(get(url))
                .andExpect(status().isOk());
        String resultString = resultActions.andReturn().getResponse().getContentAsString();
        CollectionType listType = objectMapper.getTypeFactory().constructCollectionType(List.class, ChatDTO.class);
        List<ChatDTO> resultDTOList = objectMapper.readValue(resultString, listType);
        assertThat(resultDTOList).isNotEmpty();
    }

    @Test
    @WithCustomSecurityContext(id = USER_ID_1)
    void testGetFiltered_ok_byUsername() throws Exception {
        when(userServiceClient.getAllIdsByUsernamePart("test_user"))
                .thenReturn(Collections.singletonList(UUID.fromString(USER_ID_4)));
        String url = ENDPOINT + "/filtered/test_user";
        ResultActions resultActions = mvc.perform(get(url))
                .andExpect(status().isOk());
        String resultString = resultActions.andReturn().getResponse().getContentAsString();
        CollectionType listType = objectMapper.getTypeFactory().constructCollectionType(List.class, ChatDTO.class);
        List<ChatDTO> resultDTOList = objectMapper.readValue(resultString, listType);
        assertThat(resultDTOList).isNotEmpty();
    }

    @Test
    @WithCustomSecurityContext(id = USER_ID_1)
    void testGetFiltered_ok_empty() throws Exception {
        String url = ENDPOINT + "/filtered/not_found";
        ResultActions resultActions = mvc.perform(get(url))
                .andExpect(status().isOk());
        String resultString = resultActions.andReturn().getResponse().getContentAsString();
        CollectionType listType = objectMapper.getTypeFactory().constructCollectionType(List.class, ChatDTO.class);
        List<ChatDTO> resultDTOList = objectMapper.readValue(resultString, listType);
        assertThat(resultDTOList).isEmpty();
    }

    @Test
    @WithAnonymousUser
    void testGetFiltered_unauthorized() throws Exception {
        String url = ENDPOINT + "/filtered/test";
        mvc.perform(get(url))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithCustomSecurityContext(id = USER_ID_1)
    void testGetById_ok() throws Exception {
        String url = ENDPOINT + "/id/" + chat1.getId().toString();
        ResultActions resultActions = mvc.perform(get(url))
                .andExpect(status().isOk());
        String resultString = resultActions.andReturn().getResponse().getContentAsString();
        ChatDTO resultDTO = objectMapper.readValue(resultString, ChatDTO.class);
        assertThat(resultDTO.getId()).isEqualTo(chat1.getId());
    }

    @Test
    @WithCustomSecurityContext(id = USER_ID_1)
    void testGetById_badRequest_noPermissions() throws Exception {
        String url = ENDPOINT + "/id/" + chat2.getId().toString();
        mvc.perform(get(url))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithCustomSecurityContext(id = USER_ID_1)
    void testGetById_notFound() throws Exception {
        String url = ENDPOINT + "/id/" + UUID.randomUUID();
        mvc.perform(get(url))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithAnonymousUser
    void testGetById_unauthorized() throws Exception {
        String url = ENDPOINT + "/id/" + UUID.randomUUID();
        mvc.perform(get(url))
                .andExpect(status().isUnauthorized());
    }


    @Test
    @WithCustomSecurityContext(id = USER_ID_1)
    void testCreateDirect_ok() throws Exception {
        UUID userId = UUID.fromString(USER_ID_3);
        String url = ENDPOINT + "/create-direct/" + userId;
        ResultActions resultActions = mvc.perform(get(url))
                .andExpect(status().isCreated());
        String resultString = resultActions.andReturn().getResponse().getContentAsString();
        ChatDTO resultDTO = objectMapper.readValue(resultString, ChatDTO.class);
        assertThat(resultDTO.getMembers()).contains(UUID.fromString(USER_ID_1), UUID.fromString(USER_ID_3));
        assertThat(resultDTO.isDirect()).isTrue();
    }

    @Test
    @WithCustomSecurityContext(id = USER_ID_1)
    void testCreateDirect_badRequest_alreadyExists() throws Exception {
        UUID userId = UUID.fromString(USER_ID_4);
        String url = ENDPOINT + "/create-direct/" + userId;
        mvc.perform(get(url))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithCustomSecurityContext(id = USER_ID_1)
    void testCreateDirect_notFound() throws Exception {
        when(userServiceClient.doesIdExist(any())).thenReturn(false);
        UUID userId = UUID.randomUUID();
        String url = ENDPOINT + "/create-direct/" + userId;
        mvc.perform(get(url))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithAnonymousUser
    void testCreateDirect_unauthorized() throws Exception {
        UUID userId = UUID.fromString(USER_ID_2);
        String url = ENDPOINT + "/create-direct/" + userId;
        mvc.perform(get(url))
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
        String url = ENDPOINT + "/rename/" + UUID.randomUUID();
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


    private Chat createChat(String title, boolean isDirect, String... userIds) {
        Chat chat = TestChat.defaultBuilder().title(title).isDirect(isDirect).build().toParent();
        Chat savedChat = chatRepository.saveAndFlush(chat);
        createAddMemberEvents(savedChat, userIds);
        createStubMessages(savedChat, userIds);
        return savedChat;
    }

    private void createAddMemberEvents(Chat chat, String... userIds) {
        List<MemberEvent> messageList = Arrays.stream(userIds)
                .map(UUID::fromString)
                .map(userId -> new MemberEvent(chat, userId, MemberEventType.ADD_MEMBER))
                .collect(Collectors.toList());
        memberEventRepository.saveAll(messageList);
        memberEventRepository.flush();
    }

    private void createStubMessages(Chat chat, String... userIds) {
        List<Message> messageList = Arrays.stream(userIds)
                .map(userId -> Message.stub(chat, UUID.fromString(userId)))
                .collect(Collectors.toList());
        messageRepository.saveAll(messageList);
        messageRepository.flush();
    }

}
