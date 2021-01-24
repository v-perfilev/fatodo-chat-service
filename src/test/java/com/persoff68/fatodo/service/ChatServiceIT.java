package com.persoff68.fatodo.service;

import com.persoff68.fatodo.FatodoMessageServiceApplication;
import com.persoff68.fatodo.client.UserServiceClient;
import com.persoff68.fatodo.model.Chat;
import com.persoff68.fatodo.model.MemberEvent;
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

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

@SpringBootTest(classes = FatodoMessageServiceApplication.class)
public class ChatServiceIT {

    private static final UUID USER_1_ID = UUID.fromString("98a4f736-70c2-4c7d-b75b-f7a5ae7bbe8d");
    private static final UUID USER_2_ID = UUID.fromString("8d583dfd-acfb-4481-80e6-0b46170e2a18");
    private static final UUID USER_3_ID = UUID.fromString("5b8bfe7e-7651-4d39-a70c-22c997e376b1");

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

    @MockBean
    UserServiceClient userServiceClient;

    MockMvc mvc;

    @BeforeEach
    public void setup() {
        mvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();

        when(userServiceClient.doesIdExist(any())).thenReturn(true);

        chatRepository.deleteAll();
        memberEventRepository.deleteAll();
        messageRepository.deleteAll();

        Chat firstChat = chatService.createDirect(USER_1_ID, USER_2_ID);
        Chat secondChat = chatService.createDirect(USER_2_ID, USER_3_ID);
        Chat thirdChat = chatService.createDirect(USER_3_ID, USER_1_ID);

        for (int i = 0; i < 2; i++) {
            messageService.sendDirect(USER_1_ID, USER_2_ID, UUID.randomUUID().toString(), null);
        }
        for (int i = 0; i < 2; i++) {
            messageService.sendDirect(USER_2_ID, USER_3_ID, UUID.randomUUID().toString(), null);
        }
        for (int i = 0; i < 2; i++) {
            messageService.send( USER_3_ID,thirdChat.getId(), UUID.randomUUID().toString(), null);
        }
    }

    @Test
    public void getAllByUserIdTest() {
        List<MemberEvent> memberEventList = memberEventRepository.findAll();
        System.out.println(memberEventList);
        Pageable pageable = PageRequest.of(0, 100);
        Map<Chat, Message> chatMap = chatService.getAllByUserId(USER_1_ID, pageable);
        assertThat(chatMap.size()).isEqualTo(2);
    }


}
