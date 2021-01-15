package com.persoff68.fatodo.service;

import com.persoff68.fatodo.FatodoMessageServiceApplication;
import com.persoff68.fatodo.builder.TestChat;
import com.persoff68.fatodo.builder.TestMemberEvent;
import com.persoff68.fatodo.builder.TestMessage;
import com.persoff68.fatodo.model.Chat;
import com.persoff68.fatodo.model.MemberEvent;
import com.persoff68.fatodo.model.Message;
import com.persoff68.fatodo.repository.ChatRepository;
import com.persoff68.fatodo.repository.MemberRepository;
import com.persoff68.fatodo.repository.MessageRepository;
import com.persoff68.fatodo.service.exception.ModelNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
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
    MemberRepository memberRepository;
    @Autowired
    MessageRepository messageRepository;
    @Autowired
    ChatService chatService;

    MockMvc mvc;

    @BeforeEach
    public void setup() {
        mvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();

        memberRepository.deleteAll();
        chatRepository.deleteAll();

        Chat firstChat = saveChat(USER_1_ID, USER_2_ID);
        saveChat(USER_1_ID, USER_3_ID);
        saveChat(USER_2_ID, USER_3_ID);
        for (int i = 0; i < 100; i++) {
            saveMessage(firstChat.getId(), USER_1_ID, UUID.randomUUID().toString());
        }
//        List<Message> messageList = messageRepository.findAllByChatId(firstChat.getId());
//        firstChat.setMessages(messageList);
//        chatRepository.save(firstChat);
    }

    @Test
    @Transactional
    public void getAllByUserIdTest() {
        Pageable pageable = PageRequest.of(0, 100);
        List<Chat> chatList = chatService.getAllByUserId(USER_1_ID, pageable);
        int size = chatList.stream()
                .map(chat -> chat.getMessages().size())
                .reduce(Integer::sum)
                .orElse(0);
        List<Message> messageList = messageRepository.findAll();
        assertThat(messageList.size()).isEqualTo(100);
        assertThat(chatList.size()).isEqualTo(2);
        assertThat(size).isEqualTo(100);
    }

    @Transactional
    protected Chat saveChat(UUID... userIds) {
        Chat chat = TestChat.defaultBuilder().build().toParent();
        chat = chatRepository.save(chat);
        UUID chatId = chat.getId();
        List<MemberEvent> memberEventList = Arrays.stream(userIds)
                .map(userId -> TestMemberEvent.defaultBuilder()
                        .chatId(chatId)
                        .userId(userId)
                        .type(MemberEvent.Type.ADD_MEMBER)
                        .build().toParent())
                .collect(Collectors.toList());
        chat.setMemberEvents(memberEventList);
        return chatRepository.save(chat);
    }

    @Transactional
    protected Message saveMessage(UUID chatId, UUID userId, String text) {
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(ModelNotFoundException::new);
        Message message = TestMessage.defaultBuilder()
                .chat(chat)
                .userId(userId)
                .text(text)
                .build().toParent();
        return messageRepository.save(message);
    }

}
