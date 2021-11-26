package com.persoff68.fatodo.service;

import com.persoff68.fatodo.FatodoChatServiceApplication;
import com.persoff68.fatodo.client.ContactServiceClient;
import com.persoff68.fatodo.client.UserServiceClient;
import com.persoff68.fatodo.client.WsServiceClient;
import com.persoff68.fatodo.model.Chat;
import com.persoff68.fatodo.model.Message;
import com.persoff68.fatodo.repository.ChatRepository;
import com.persoff68.fatodo.repository.MemberEventRepository;
import com.persoff68.fatodo.repository.MessageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

@SpringBootTest(classes = FatodoChatServiceApplication.class)
public class MessageServicesIT {

    private static final UUID USER_1_ID = UUID.fromString("98a4f736-70c2-4c7d-b75b-f7a5ae7bbe8d");
    private static final UUID USER_2_ID = UUID.fromString("8d583dfd-acfb-4481-80e6-0b46170e2a18");
    private static final UUID USER_3_ID = UUID.fromString("5b8bfe7e-7651-4d39-a70c-22c997e376b1");

    private static final Pageable pageable = PageRequest.of(0, 100);

    private Chat firstChat;
    private Chat secondChat;

    @Autowired
    WebApplicationContext context;

    @Autowired
    ChatRepository chatRepository;
    @Autowired
    MemberEventRepository memberEventRepository;
    @Autowired
    MessageRepository messageRepository;

    @Autowired
    ChatService chatService;
    @Autowired
    MessageService messageService;
    @Autowired
    MemberEventService memberEventService;

    @MockBean
    UserServiceClient userServiceClient;
    @MockBean
    ContactServiceClient contactServiceClient;
    @MockBean
    WsServiceClient wsServiceClient;

    MockMvc mvc;

    @BeforeEach
    public void setup() {
        mvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();

        when(userServiceClient.doIdsExist(any())).thenReturn(true);
        when(contactServiceClient.areUsersInContactList(any())).thenReturn(true);
        doNothing().when(wsServiceClient).sendChatNewEvent(any());

        chatRepository.deleteAll();
        memberEventRepository.deleteAll();
        messageRepository.deleteAll();

        // create chats
        firstChat = chatService.createDirect(USER_1_ID, USER_2_ID);
        secondChat = chatService.createIndirect(USER_1_ID, List.of(USER_2_ID, USER_3_ID));

        // init with messages
        messageService.send(USER_1_ID, firstChat.getId(), UUID.randomUUID().toString(), null);
        messageService.send(USER_1_ID, secondChat.getId(), UUID.randomUUID().toString(), null);
    }

    @Test
    public void getAllMessagesTest() {
        List<Message> firstUserFirstChatMessageList = messageService
                .getAllByUserIdAndChatId(USER_1_ID, firstChat.getId(), pageable);
        List<Message> firstUserSecondChatMessageList = messageService
                .getAllByUserIdAndChatId(USER_1_ID, secondChat.getId(), pageable);

        assertThat(firstUserFirstChatMessageList.size()).isEqualTo(2);
        assertThat(firstUserSecondChatMessageList.size()).isEqualTo(2);
    }

    private void beforeLeaveAndGetAllMessagesTest() {
        // leave second chat
        memberEventService.leaveChat(USER_1_ID, secondChat.getId());
        // message to second chat
        messageService.send(USER_2_ID, secondChat.getId(), UUID.randomUUID().toString(), null);
        // enter second chat
        memberEventService.addUsers(USER_2_ID, secondChat.getId(), Collections.singletonList(USER_1_ID));
        // message to second chat
        messageService.send(USER_1_ID, secondChat.getId(), UUID.randomUUID().toString(), null);
    }

    @Test
    public void leaveAndGetAllMessagesTest() {
        beforeLeaveAndGetAllMessagesTest();

        List<Message> firstUserSecondChatMessageList = messageService
                .getAllByUserIdAndChatId(USER_1_ID, secondChat.getId(), pageable);
        List<Message> secondUserSecondChatMessageList = messageService
                .getAllByUserIdAndChatId(USER_2_ID, secondChat.getId(), pageable);

        assertThat(firstUserSecondChatMessageList.size()).isEqualTo(5);
        assertThat(secondUserSecondChatMessageList.size()).isEqualTo(6);
    }

    private void beforeClearAndGetAllMessagesTest() {
        // clear second chat
        memberEventService.clearChat(USER_1_ID, secondChat.getId());

        // messages to second chat
        messageService.send(USER_2_ID, secondChat.getId(), UUID.randomUUID().toString(), null);
    }

    @Test
    public void clearAndGetAllMessagesTest() {
        beforeClearAndGetAllMessagesTest();

        List<Message> firstUserSecondChatMessageList = messageService
                .getAllByUserIdAndChatId(USER_1_ID, secondChat.getId(), pageable);
        List<Message> secondUserSecondChatMessageList = messageService
                .getAllByUserIdAndChatId(USER_2_ID, secondChat.getId(), pageable);

        assertThat(firstUserSecondChatMessageList.size()).isEqualTo(1);
        assertThat(secondUserSecondChatMessageList.size()).isEqualTo(3);
    }

}
