package com.persoff68.fatodo.web.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.persoff68.fatodo.FatodoMessageServiceApplication;
import com.persoff68.fatodo.annotation.WithCustomSecurityContext;
import com.persoff68.fatodo.builder.TestChat;
import com.persoff68.fatodo.builder.TestMemberEvent;
import com.persoff68.fatodo.client.UserServiceClient;
import com.persoff68.fatodo.model.Chat;
import com.persoff68.fatodo.model.MemberEvent;
import com.persoff68.fatodo.model.MemberEventType;
import com.persoff68.fatodo.repository.ChatRepository;
import com.persoff68.fatodo.repository.MemberEventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = FatodoMessageServiceApplication.class)
public class MemberControllerIT {
    private static final String ENDPOINT = "/api/member";

    private static final String USER_ID_1 = "3c300277-b5ea-48d1-80db-ead620cf5846";
    private static final String USER_ID_2 = "357a2a99-7b7e-4336-9cd7-18f2cf73fab9";
    private static final String USER_ID_3 = "71bae736-415b-474c-9865-29043cbc8d0c";
    private static final String USER_ID_4 = "516d2fee-ea1e-4599-b151-1743ab668d6e";

    private Chat chat1;
    private Chat chat2;

    @Autowired
    WebApplicationContext context;
    @Autowired
    ChatRepository chatRepository;
    @Autowired
    MemberEventRepository memberEventRepository;
    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    UserServiceClient userServiceClient;

    MockMvc mvc;

    @BeforeEach
    public void setup() {
        mvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();

        chatRepository.deleteAll();
        memberEventRepository.deleteAll();

        chat1 = createIndirectChat();
        createMemberEvents(chat1, USER_ID_1, USER_ID_2);

        chat2 = createIndirectChat();
        createMemberEvents(chat2, USER_ID_2, USER_ID_3);

        when(userServiceClient.doesIdExist(any())).thenReturn(true);
    }


    @Test
    @WithCustomSecurityContext(id = USER_ID_1)
    public void testAddUsers_ok_one() throws Exception {
        String url = ENDPOINT + "/add/" + chat1.getId().toString();
        List<UUID> userIdList = List.of(UUID.fromString(USER_ID_3));
        String requestBody = objectMapper.writeValueAsString(userIdList);
        mvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isOk());
        List<MemberEvent> memberEventList = memberEventRepository.findAll();
        List<MemberEvent> filteredMemberEventList = memberEventList.stream()
                .filter(memberEvent -> memberEvent.getChat().getId().equals(chat1.getId())
                        && memberEvent.getType().equals(MemberEventType.ADD_MEMBER)
                        && memberEvent.getUserId().equals(UUID.fromString(USER_ID_3)))
                .collect(Collectors.toList());
        assertThat(filteredMemberEventList.size()).isEqualTo(1);
    }

    @Test
    @WithCustomSecurityContext(id = USER_ID_1)
    public void testAddUsers_ok_many() throws Exception {
        String url = ENDPOINT + "/add/" + chat1.getId().toString();
        List<UUID> userIdList = List.of(UUID.fromString(USER_ID_3), UUID.fromString(USER_ID_4));
        String requestBody = objectMapper.writeValueAsString(userIdList);
        mvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isOk());
        List<MemberEvent> memberEventList = memberEventRepository.findAll();
        List<MemberEvent> filteredMemberEventList = memberEventList.stream()
                .filter(memberEvent -> memberEvent.getChat().getId().equals(chat1.getId())
                        && memberEvent.getType().equals(MemberEventType.ADD_MEMBER)
                        && (memberEvent.getUserId().equals(UUID.fromString(USER_ID_3))
                        || memberEvent.getUserId().equals(UUID.fromString(USER_ID_4))))
                .collect(Collectors.toList());
        assertThat(filteredMemberEventList.size()).isEqualTo(2);
    }

    @Test
    @WithCustomSecurityContext(id = USER_ID_1)
    public void testAddUsers_notFound_user() throws Exception {
        when(userServiceClient.doesIdExist(any())).thenReturn(false);
        String url = ENDPOINT + "/add/" + chat1.getId().toString();
        List<UUID> userIdList = List.of(UUID.randomUUID());
        String requestBody = objectMapper.writeValueAsString(userIdList);
        mvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithCustomSecurityContext(id = USER_ID_1)
    public void testAddUsers_notFound_chat() throws Exception {
        String url = ENDPOINT + "/add/" + UUID.randomUUID().toString();
        List<UUID> userIdList = List.of(UUID.fromString(USER_ID_3));
        String requestBody = objectMapper.writeValueAsString(userIdList);
        mvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithCustomSecurityContext(id = USER_ID_1)
    public void testAddUsers_badRequest_noPermissions() throws Exception {
        String url = ENDPOINT + "/add/" + chat2.getId().toString();
        List<UUID> userIdList = List.of(UUID.fromString(USER_ID_4));
        String requestBody = objectMapper.writeValueAsString(userIdList);
        mvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithAnonymousUser
    void testAddUsers_unauthorized() throws Exception {
        String url = ENDPOINT + "/add/" + chat1.getId().toString();
        List<UUID> userIdList = List.of(UUID.fromString(USER_ID_3));
        String requestBody = objectMapper.writeValueAsString(userIdList);
        mvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isUnauthorized());
    }


    @Test
    @WithCustomSecurityContext(id = USER_ID_1)
    public void testRemoveUsers_ok_one() throws Exception {
        createMemberEvents(chat1, USER_ID_3, USER_ID_4);
        String url = ENDPOINT + "/remove/" + chat1.getId().toString();
        List<UUID> userIdList = List.of(UUID.fromString(USER_ID_3));
        String requestBody = objectMapper.writeValueAsString(userIdList);
        mvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isOk());
        List<MemberEvent> memberEventList = memberEventRepository.findAll();
        List<MemberEvent> filteredMemberEventList = memberEventList.stream()
                .filter(memberEvent -> memberEvent.getChat().getId().equals(chat1.getId())
                        && (memberEvent.getType().equals(MemberEventType.ADD_MEMBER)
                        || memberEvent.getType().equals(MemberEventType.DELETE_MEMBER))
                        && memberEvent.getUserId().equals(UUID.fromString(USER_ID_3)))
                .collect(Collectors.toList());
        assertThat(filteredMemberEventList.size()).isEqualTo(2);
    }

    @Test
    @WithCustomSecurityContext(id = USER_ID_1)
    public void testRemoveUsers_ok_many() throws Exception {
        createMemberEvents(chat1, USER_ID_3, USER_ID_4);
        String url = ENDPOINT + "/remove/" + chat1.getId().toString();
        List<UUID> userIdList = List.of(UUID.fromString(USER_ID_3), UUID.fromString(USER_ID_4));
        String requestBody = objectMapper.writeValueAsString(userIdList);
        mvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isOk());
        List<MemberEvent> memberEventList = memberEventRepository.findAll();
        List<MemberEvent> filteredMemberEventList = memberEventList.stream()
                .filter(memberEvent -> memberEvent.getChat().getId().equals(chat1.getId())
                        && (memberEvent.getType().equals(MemberEventType.ADD_MEMBER)
                        || memberEvent.getType().equals(MemberEventType.DELETE_MEMBER))
                        && (memberEvent.getUserId().equals(UUID.fromString(USER_ID_3))
                        || memberEvent.getUserId().equals(UUID.fromString(USER_ID_4))))
                .collect(Collectors.toList());
        assertThat(filteredMemberEventList.size()).isEqualTo(4);
    }

    @Test
    @WithCustomSecurityContext(id = USER_ID_1)
    public void testRemoveUsers_notFound_user() throws Exception {
        String url = ENDPOINT + "/remove/" + chat1.getId().toString();
        List<UUID> userIdList = List.of(UUID.fromString(USER_ID_3));
        String requestBody = objectMapper.writeValueAsString(userIdList);
        mvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithCustomSecurityContext(id = USER_ID_1)
    public void testRemoveUsers_notFound_chat() throws Exception {
        String url = ENDPOINT + "/remove/" + UUID.randomUUID().toString();
        List<UUID> userIdList = List.of(UUID.fromString(USER_ID_3));
        String requestBody = objectMapper.writeValueAsString(userIdList);
        mvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithCustomSecurityContext(id = USER_ID_1)
    public void testRemoveUsers_badRequest_noPermissions() throws Exception {
        String url = ENDPOINT + "/remove/" + chat2.getId().toString();
        List<UUID> userIdList = List.of(UUID.fromString(USER_ID_3));
        String requestBody = objectMapper.writeValueAsString(userIdList);
        mvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithAnonymousUser
    void testRemoveUsers_unauthorized() throws Exception {
        String url = ENDPOINT + "/remove/" + chat1.getId().toString();
        List<UUID> userIdList = List.of(UUID.fromString(USER_ID_3));
        String requestBody = objectMapper.writeValueAsString(userIdList);
        mvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isUnauthorized());
    }


    private Chat createIndirectChat() {
        Chat chat = TestChat.defaultBuilder().isDirect(false).build().toParent();
        return chatRepository.save(chat);
    }

    private void createMemberEvents(Chat chat, String... userIds) {
        List<MemberEvent> memberEventList = Arrays.stream(userIds)
                .map(id -> TestMemberEvent.defaultBuilder()
                        .chat(chat).userId(UUID.fromString(id)).build().toParent())
                .collect(Collectors.toList());
        memberEventRepository.saveAll(memberEventList);
    }

}