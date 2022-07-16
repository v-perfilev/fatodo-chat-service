package com.persoff68.fatodo.service;

import com.persoff68.fatodo.FatodoChatServiceApplication;
import com.persoff68.fatodo.client.ContactServiceClient;
import com.persoff68.fatodo.client.EventServiceClient;
import com.persoff68.fatodo.client.UserServiceClient;
import com.persoff68.fatodo.client.WsServiceClient;
import com.persoff68.fatodo.model.Chat;
import com.persoff68.fatodo.model.Message;
import com.persoff68.fatodo.repository.ChatRepository;
import com.persoff68.fatodo.repository.MemberEventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = FatodoChatServiceApplication.class)
@AutoConfigureMockMvc
class ChatServicesIT {

    private static final UUID USER_1_ID = UUID.fromString("98a4f736-70c2-4c7d-b75b-f7a5ae7bbe8d");
    private static final UUID USER_2_ID = UUID.fromString("8d583dfd-acfb-4481-80e6-0b46170e2a18");
    private static final UUID USER_3_ID = UUID.fromString("5b8bfe7e-7651-4d39-a70c-22c997e376b1");

    private static final Pageable pageable = PageRequest.of(0, 100);


    @Autowired
    MockMvc mvc;

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
    ContactServiceClient contactServiceClient;
    @MockBean
    WsServiceClient wsServiceClient;
    @MockBean
    EventServiceClient eventServiceClient;


    private Chat secondChat;

    @BeforeEach
    void setup() {

        when(userServiceClient.doesIdExist(any())).thenReturn(true);
        when(userServiceClient.doIdsExist(any())).thenReturn(true);
        when(contactServiceClient.areUsersInContactList(any())).thenReturn(true);
        doNothing().when(wsServiceClient).sendChatNewEvent(any());
        doNothing().when(eventServiceClient).deleteChatEventsForUser(any());

        chatRepository.deleteAll();
        memberEventRepository.deleteAll();

        // create chats
        chatService.createDirect(USER_1_ID, USER_2_ID);
        secondChat = chatService.createIndirect(USER_1_ID, List.of(USER_2_ID, USER_3_ID));
    }

    @Test
    @Transactional
    void getAllChatsByUserIdTest() {
        Map<Chat, Message> firstUserChatMap = chatService.getAllByUserId(USER_1_ID, pageable);
        Map<Chat, Message> secondUserChatMap = chatService.getAllByUserId(USER_2_ID, pageable);

        assertThat(firstUserChatMap).hasSize(2);
        assertThat(secondUserChatMap).hasSize(2);
    }

    private void beforeLeaveAndGetAllChatsByUserIdTest() {
        memberEventService.leaveChat(USER_1_ID, secondChat.getId());
    }

    @Test
    void leaveAndGetAllChatsByUserIdTest() {
        beforeLeaveAndGetAllChatsByUserIdTest();

        Map<Chat, Message> firstUserChatMap = chatService.getAllByUserId(USER_1_ID, pageable);
        Map<Chat, Message> secondUserChatMap = chatService.getAllByUserId(USER_2_ID, pageable);

        assertThat(firstUserChatMap).hasSize(2);
        assertThat(secondUserChatMap).hasSize(2);
    }

    private void beforeClearAndGetAllChatsByUserIdTest() {
        memberEventService.clearChat(USER_1_ID, secondChat.getId());
    }

    @Test
    @Transactional
    void clearAndGetAllChatsByUserIdTest() {
        beforeClearAndGetAllChatsByUserIdTest();

        Map<Chat, Message> firstUserChatMap = chatService.getAllByUserId(USER_1_ID, pageable);
        Map<Chat, Message> secondUserChatMap = chatService.getAllByUserId(USER_2_ID, pageable);

        assertThat(firstUserChatMap).hasSize(2);
        assertThat(secondUserChatMap).hasSize(2);
    }

    private void beforeDeleteAndGetAllChatsByUserIdTest() {
        memberEventService.deleteChat(USER_1_ID, secondChat.getId());
    }

    @Test
    @Transactional
    void deleteAndGetAllChatsByUserIdTest() {
        beforeDeleteAndGetAllChatsByUserIdTest();

        Map<Chat, Message> firstUserChatMap = chatService.getAllByUserId(USER_1_ID, pageable);
        Map<Chat, Message> secondUserChatMap = chatService.getAllByUserId(USER_2_ID, pageable);

        assertThat(firstUserChatMap).hasSize(1);
        assertThat(secondUserChatMap).hasSize(2);
    }

}
