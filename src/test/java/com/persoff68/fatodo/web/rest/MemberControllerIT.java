package com.persoff68.fatodo.web.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.persoff68.fatodo.FatodoChatServiceApplication;
import com.persoff68.fatodo.annotation.WithCustomSecurityContext;
import com.persoff68.fatodo.builder.TestChat;
import com.persoff68.fatodo.builder.TestMemberEvent;
import com.persoff68.fatodo.client.ContactServiceClient;
import com.persoff68.fatodo.client.EventServiceClient;
import com.persoff68.fatodo.client.UserServiceClient;
import com.persoff68.fatodo.client.WsServiceClient;
import com.persoff68.fatodo.model.Chat;
import com.persoff68.fatodo.model.MemberEvent;
import com.persoff68.fatodo.model.constant.MemberEventType;
import com.persoff68.fatodo.repository.ChatRepository;
import com.persoff68.fatodo.repository.MemberEventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.web.servlet.MockMvc;

import javax.persistence.EntityManager;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = FatodoChatServiceApplication.class)
@AutoConfigureMockMvc
class MemberControllerIT {
    private static final String ENDPOINT = "/api/members";

    private static final String USER_ID_1 = "3c300277-b5ea-48d1-80db-ead620cf5846";
    private static final String USER_ID_2 = "357a2a99-7b7e-4336-9cd7-18f2cf73fab9";
    private static final String USER_ID_3 = "71bae736-415b-474c-9865-29043cbc8d0c";
    private static final String USER_ID_4 = "516d2fee-ea1e-4599-b151-1743ab668d6e";

    private Chat chat1;
    private Chat chat2;

    @Autowired
    MockMvc mvc;
    @Autowired
    ChatRepository chatRepository;
    @Autowired
    MemberEventRepository memberEventRepository;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    EntityManager entityManager;

    @MockBean
    UserServiceClient userServiceClient;
    @MockBean
    ContactServiceClient contactServiceClient;
    @MockBean
    WsServiceClient wsServiceClient;
    @MockBean
    EventServiceClient eventServiceClient;

    @BeforeEach
    void setup() {
        chatRepository.deleteAll();
        memberEventRepository.deleteAll();

        chat1 = createIndirectChat(USER_ID_1, USER_ID_2);
        chat2 = createIndirectChat(USER_ID_2, USER_ID_3);

        when(userServiceClient.doIdsExist(any())).thenReturn(true);
        when(contactServiceClient.areUsersInContactList(any())).thenReturn(true);
        doNothing().when(wsServiceClient).sendChatUpdateEvent(any());
        doNothing().when(eventServiceClient).addChatEvent(any());
        doNothing().when(eventServiceClient).deleteChatEventsForUser(any());
    }


    @Test
    @WithCustomSecurityContext(id = USER_ID_1)
    void testAddUsers_ok_one() throws Exception {
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
                .toList();
        assertThat(filteredMemberEventList).hasSize(1);
    }

    @Test
    @WithCustomSecurityContext(id = USER_ID_1)
    void testAddUsers_ok_many() throws Exception {
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
                .toList();
        assertThat(filteredMemberEventList).hasSize(2);
    }

    @Test
    @WithCustomSecurityContext(id = USER_ID_1)
    void testAddUsers_notAllowedUsers() throws Exception {
        when(contactServiceClient.areUsersInContactList(any())).thenReturn(false);
        String url = ENDPOINT + "/add/" + chat1.getId().toString();
        List<UUID> userIdList = List.of(UUID.randomUUID());
        String requestBody = objectMapper.writeValueAsString(userIdList);
        mvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithCustomSecurityContext(id = USER_ID_1)
    void testAddUsers_notFound_chat() throws Exception {
        String url = ENDPOINT + "/add/" + UUID.randomUUID();
        List<UUID> userIdList = List.of(UUID.fromString(USER_ID_3));
        String requestBody = objectMapper.writeValueAsString(userIdList);
        mvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithCustomSecurityContext(id = USER_ID_1)
    void testAddUsers_forbidden() throws Exception {
        String url = ENDPOINT + "/add/" + chat2.getId().toString();
        List<UUID> userIdList = List.of(UUID.fromString(USER_ID_4));
        String requestBody = objectMapper.writeValueAsString(userIdList);
        mvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isForbidden());
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
    void testRemoveUsers_ok_one() throws Exception {
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
                .toList();
        assertThat(filteredMemberEventList).hasSize(2);
    }

    @Test
    @WithCustomSecurityContext(id = USER_ID_1)
    void testRemoveUsers_ok_many() throws Exception {
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
                .toList();
        assertThat(filteredMemberEventList).hasSize(4);
    }

    @Test
    @WithCustomSecurityContext(id = USER_ID_1)
    void testRemoveUsers_notFound_user() throws Exception {
        String url = ENDPOINT + "/remove/" + chat1.getId().toString();
        List<UUID> userIdList = List.of(UUID.fromString(USER_ID_3));
        String requestBody = objectMapper.writeValueAsString(userIdList);
        mvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithCustomSecurityContext(id = USER_ID_1)
    void testRemoveUsers_notFound_chat() throws Exception {
        String url = ENDPOINT + "/remove/" + UUID.randomUUID();
        List<UUID> userIdList = List.of(UUID.fromString(USER_ID_3));
        String requestBody = objectMapper.writeValueAsString(userIdList);
        mvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithCustomSecurityContext(id = USER_ID_1)
    void testRemoveUsers_forbidden() throws Exception {
        String url = ENDPOINT + "/remove/" + chat2.getId().toString();
        List<UUID> userIdList = List.of(UUID.fromString(USER_ID_3));
        String requestBody = objectMapper.writeValueAsString(userIdList);
        mvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isForbidden());
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


    @Test
    @WithCustomSecurityContext(id = USER_ID_1)
    void testLeave_ok() throws Exception {
        String url = ENDPOINT + "/leave/" + chat1.getId().toString();
        mvc.perform(get(url))
                .andExpect(status().isOk());
        List<MemberEvent> memberEventList = memberEventRepository.findAll();
        List<MemberEvent> filteredMemberEventList = memberEventList.stream()
                .filter(memberEvent -> memberEvent.getChat().getId().equals(chat1.getId())
                        && memberEvent.getType().equals(MemberEventType.LEAVE_CHAT)
                        && memberEvent.getUserId().equals(UUID.fromString(USER_ID_1)))
                .toList();
        assertThat(filteredMemberEventList).hasSize(1);
    }

    @Test
    @WithCustomSecurityContext(id = USER_ID_1)
    void testLeave_forbidden() throws Exception {
        String url = ENDPOINT + "/leave/" + chat2.getId().toString();
        mvc.perform(get(url))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithCustomSecurityContext(id = USER_ID_1)
    void testLeave_notFound() throws Exception {
        String url = ENDPOINT + "/leave/" + UUID.randomUUID();
        mvc.perform(get(url))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithAnonymousUser
    void testLeave_unauthorized() throws Exception {
        String url = ENDPOINT + "/leave/" + chat1.getId().toString();
        mvc.perform(get(url))
                .andExpect(status().isUnauthorized());
    }


    @Test
    @WithCustomSecurityContext(id = USER_ID_1)
    void testClear_ok() throws Exception {
        String url = ENDPOINT + "/clear/" + chat1.getId().toString();
        mvc.perform(get(url))
                .andExpect(status().isOk());
        List<MemberEvent> memberEventList = memberEventRepository.findAll();
        List<MemberEvent> filteredMemberEventList = memberEventList.stream()
                .filter(memberEvent -> memberEvent.getChat().getId().equals(chat1.getId())
                        && memberEvent.getType().equals(MemberEventType.CLEAR_CHAT)
                        && memberEvent.getUserId().equals(UUID.fromString(USER_ID_1)))
                .toList();
        assertThat(filteredMemberEventList).hasSize(1);
    }

    @Test
    @WithCustomSecurityContext(id = USER_ID_1)
    void testClear_forbidden() throws Exception {
        String url = ENDPOINT + "/clear/" + chat2.getId().toString();
        mvc.perform(get(url))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithCustomSecurityContext(id = USER_ID_1)
    void testClear_notFound() throws Exception {
        String url = ENDPOINT + "/clear/" + UUID.randomUUID();
        mvc.perform(get(url))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithAnonymousUser
    void testClear_unauthorized() throws Exception {
        String url = ENDPOINT + "/clear/" + chat1.getId().toString();
        mvc.perform(get(url))
                .andExpect(status().isUnauthorized());
    }


    @Test
    @WithCustomSecurityContext(id = USER_ID_1)
    void testDelete_ok() throws Exception {
        String url = ENDPOINT + "/delete/" + chat1.getId().toString();
        mvc.perform(get(url))
                .andExpect(status().isOk());
        List<MemberEvent> memberEventList = memberEventRepository.findAll();
        List<MemberEvent> filteredMemberEventList = memberEventList.stream()
                .filter(memberEvent -> memberEvent.getChat().getId().equals(chat1.getId())
                        && memberEvent.getType().equals(MemberEventType.DELETE_MEMBER)
                        && memberEvent.getUserId().equals(UUID.fromString(USER_ID_1)))
                .toList();
        assertThat(filteredMemberEventList).hasSize(1);
    }

    @Test
    @WithCustomSecurityContext(id = USER_ID_1)
    void testDelete_forbidden() throws Exception {
        String url = ENDPOINT + "/delete/" + chat2.getId().toString();
        mvc.perform(get(url))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithCustomSecurityContext(id = USER_ID_1)
    void testDelete_notFound() throws Exception {
        String url = ENDPOINT + "/delete/" + UUID.randomUUID();
        mvc.perform(get(url))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithAnonymousUser
    void testDelete_unauthorized() throws Exception {
        String url = ENDPOINT + "/delete/" + chat1.getId().toString();
        mvc.perform(get(url))
                .andExpect(status().isUnauthorized());
    }


    private Chat createIndirectChat(String... userIds) {
        Chat chat = TestChat.defaultBuilder().isDirect(false).build().toParent();
        Chat savedChat = chatRepository.saveAndFlush(chat);
        createMemberEvents(savedChat, userIds);
        return savedChat;
    }

    private void createMemberEvents(Chat chat, String... userIds) {
        List<MemberEvent> memberEventList = Arrays.stream(userIds)
                .map(id -> TestMemberEvent.defaultBuilder()
                        .chat(chat).userId(UUID.fromString(id)).build().toParent())
                .toList();
        memberEventRepository.saveAll(memberEventList);
        memberEventRepository.flush();
    }

}
