package com.persoff68.fatodo.web.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.persoff68.fatodo.FatodoChatServiceApplication;
import com.persoff68.fatodo.annotation.WithCustomSecurityContext;
import com.persoff68.fatodo.builder.TestChat;
import com.persoff68.fatodo.model.Chat;
import com.persoff68.fatodo.model.MemberEvent;
import com.persoff68.fatodo.model.Message;
import com.persoff68.fatodo.model.constant.EventMessageType;
import com.persoff68.fatodo.model.constant.MemberEventType;
import com.persoff68.fatodo.repository.ChatRepository;
import com.persoff68.fatodo.repository.MemberEventRepository;
import com.persoff68.fatodo.repository.MessageRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = FatodoChatServiceApplication.class)
@AutoConfigureMockMvc
class SystemControllerIT {
    private static final String ENDPOINT = "/api/system";

    private static final String USER_ID_1 = "3c300277-b5ea-48d1-80db-ead620cf5846";
    private static final String USER_ID_2 = "357a2a99-7b7e-4336-9cd7-18f2cf73fab9";

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

    @BeforeEach
    void setup() throws Exception {
        createChat("test_chat", true, USER_ID_1, USER_ID_2);
    }

    @AfterEach
    void cleanup() {
        chatRepository.deleteAll();
        memberEventRepository.deleteAll();
    }

    @Test
    @WithCustomSecurityContext(authority = "ROLE_SYSTEM")
    void testDeleteAccountPermanently_ok() throws Exception {
        String url = ENDPOINT + "/" + USER_ID_1;
        mvc.perform(delete(url))
                .andExpect(status().isOk());
        List<Chat> user1ChatList = chatRepository.findAllByUserId(UUID.fromString(USER_ID_1));
        List<Chat> user2ChatList = chatRepository.findAllByUserId(UUID.fromString(USER_ID_2));
        assertThat(user1ChatList).isEmpty();
        assertThat(user2ChatList).hasSize(1);
    }

    @Test
    @WithCustomSecurityContext(id = USER_ID_1)
    void testGetAllPageable_ok_withParams() throws Exception {
        String url = ENDPOINT + "/" + USER_ID_1;
        mvc.perform(delete(url))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithAnonymousUser
    void testGetAllPageable_unauthorized() throws Exception {
        String url = ENDPOINT + "/" + USER_ID_1;
        mvc.perform(delete(url))
                .andExpect(status().isUnauthorized());
    }

    private void createChat(String title, boolean isDirect, String... userIds) throws Exception {
        Chat chat = TestChat.defaultBuilder().title(title).isDirect(isDirect).build().toParent();
        Chat savedChat = chatRepository.saveAndFlush(chat);
        createAddMemberEvents(savedChat, userIds);
        createEmptyMessage(savedChat, UUID.fromString(userIds[0]));
    }

    private void createAddMemberEvents(Chat chat, String... userIds) {
        List<MemberEvent> messageList = Arrays.stream(userIds)
                .map(UUID::fromString)
                .map(userId -> new MemberEvent(chat, userId, MemberEventType.ADD_MEMBER))
                .toList();
        memberEventRepository.saveAll(messageList);
        memberEventRepository.flush();
    }

    private void createEmptyMessage(Chat chat, UUID userId) throws Exception {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("type", EventMessageType.EMPTY);
        String params = objectMapper.writeValueAsString(paramMap);
        Message message = Message.event(chat, userId, params);
        messageRepository.save(message);
        messageRepository.flush();
    }

}
