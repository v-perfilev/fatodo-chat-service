package com.persoff68.fatodo.service;

import com.persoff68.fatodo.FatodoChatServiceApplication;
import com.persoff68.fatodo.client.UserServiceClient;
import com.persoff68.fatodo.client.WsServiceClient;
import com.persoff68.fatodo.model.Chat;
import com.persoff68.fatodo.model.Message;
import com.persoff68.fatodo.repository.ChatRepository;
import com.persoff68.fatodo.repository.MemberEventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

@SpringBootTest(classes = FatodoChatServiceApplication.class)
public class ChatServicesIT {

    private static final UUID USER_1_ID = UUID.fromString("98a4f736-70c2-4c7d-b75b-f7a5ae7bbe8d");
    private static final UUID USER_2_ID = UUID.fromString("8d583dfd-acfb-4481-80e6-0b46170e2a18");
    private static final UUID USER_3_ID = UUID.fromString("5b8bfe7e-7651-4d39-a70c-22c997e376b1");

    private static final Pageable pageable = PageRequest.of(0, 100);

    private Chat secondChat;

    @Autowired
    WebApplicationContext context;

    @Autowired
    ChatRepository chatRepository;
    @Autowired
    MemberEventRepository memberEventRepository;

    @Autowired
    ChatService chatService;
    @Autowired
    MemberEventService memberEventService;

    @MockBean
    UserServiceClient userServiceClient;
    @MockBean
    WsServiceClient wsServiceClient;

    MockMvc mvc;

    @BeforeEach
    public void setup() {
        mvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();

        when(userServiceClient.doesIdExist(any())).thenReturn(true);
        doNothing().when(wsServiceClient).sendChatNewEvent(any());

        chatRepository.deleteAll();
        memberEventRepository.deleteAll();

        // create chats
        chatService.createDirect(USER_1_ID, USER_2_ID);
        secondChat = chatService.createIndirect(USER_1_ID, List.of(USER_2_ID, USER_3_ID));
    }

    @Test
    public void getAllChatsByUserIdTest() {
        Map<Chat, Message> firstUserChatMap = chatService.getAllByUserId(USER_1_ID, pageable);
        Map<Chat, Message> secondUserChatMap = chatService.getAllByUserId(USER_2_ID, pageable);

        assertThat(firstUserChatMap.size()).isEqualTo(2);
        assertThat(secondUserChatMap.size()).isEqualTo(2);
    }

    private void beforeLeaveAndGetAllChatsByUserIdTest() {
        memberEventService.leaveChat(USER_1_ID, secondChat.getId());
    }

    @Test
    public void leaveAndGetAllChatsByUserIdTest() {
        beforeLeaveAndGetAllChatsByUserIdTest();

        Map<Chat, Message> firstUserChatMap = chatService.getAllByUserId(USER_1_ID, pageable);
        Map<Chat, Message> secondUserChatMap = chatService.getAllByUserId(USER_2_ID, pageable);

        assertThat(firstUserChatMap.size()).isEqualTo(2);
        assertThat(secondUserChatMap.size()).isEqualTo(2);
    }

    private void beforeClearAndGetAllChatsByUserIdTest() {
        memberEventService.clearChat(USER_1_ID, secondChat.getId());
    }

    @Test
    public void clearAndGetAllChatsByUserIdTest() {
        beforeClearAndGetAllChatsByUserIdTest();

        Map<Chat, Message> firstUserChatMap = chatService.getAllByUserId(USER_1_ID, pageable);
        Map<Chat, Message> secondUserChatMap = chatService.getAllByUserId(USER_2_ID, pageable);

        assertThat(firstUserChatMap.size()).isEqualTo(2);
        assertThat(secondUserChatMap.size()).isEqualTo(2);
    }

    private void beforeDeleteAndGetAllChatsByUserIdTest() {
        memberEventService.deleteChat(USER_1_ID, secondChat.getId());
    }

    @Test
    @Transactional
    public void deleteAndGetAllChatsByUserIdTest() {
        beforeDeleteAndGetAllChatsByUserIdTest();

        Map<Chat, Message> firstUserChatMap = chatService.getAllByUserId(USER_1_ID, pageable);
        Map<Chat, Message> secondUserChatMap = chatService.getAllByUserId(USER_2_ID, pageable);

        assertThat(firstUserChatMap.size()).isEqualTo(1);
        assertThat(secondUserChatMap.size()).isEqualTo(2);
    }

}
