package com.persoff68.fatodo.contract;

import com.persoff68.fatodo.builder.TestChat;
import com.persoff68.fatodo.builder.TestMemberEvent;
import com.persoff68.fatodo.builder.TestMessage;
import com.persoff68.fatodo.builder.TestReaction;
import com.persoff68.fatodo.client.ContactServiceClient;
import com.persoff68.fatodo.client.EventServiceClient;
import com.persoff68.fatodo.client.UserServiceClient;
import com.persoff68.fatodo.client.WsServiceClient;
import com.persoff68.fatodo.model.Chat;
import com.persoff68.fatodo.model.MemberEvent;
import com.persoff68.fatodo.model.Message;
import com.persoff68.fatodo.model.Reaction;
import com.persoff68.fatodo.model.constant.MemberEventType;
import com.persoff68.fatodo.model.constant.ReactionType;
import com.persoff68.fatodo.repository.ChatRepository;
import com.persoff68.fatodo.repository.MemberEventRepository;
import com.persoff68.fatodo.repository.MessageRepository;
import com.persoff68.fatodo.repository.ReactionRepository;
import com.persoff68.fatodo.repository.StatusRepository;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.contract.verifier.messaging.boot.AutoConfigureMessageVerifier;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import javax.persistence.EntityManager;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMessageVerifier
@Transactional
abstract class ContractBase {
    private static final UUID USER_ID_1 = UUID.fromString("8f9a7cae-73c8-4ad6-b135-5bd109b51d2e");
    private static final UUID USER_ID_2 = UUID.fromString("1b53a48c-2da5-4489-ac8a-e246c6445333");
    private static final UUID CHAT_ID = UUID.fromString("b73e8418-ff4a-472b-893d-4e248ae93797");
    private static final UUID MESSAGE_ID_1 = UUID.fromString("6796a82a-93c6-4fdf-bf5d-2da77ce2c338");
    private static final UUID MESSAGE_ID_2 = UUID.fromString("6520f3e6-0a7f-4c32-b6f8-ba5ae3ed0bd1");

    @Autowired
    WebApplicationContext context;

    @Autowired
    ChatRepository chatRepository;
    @Autowired
    MessageRepository messageRepository;
    @Autowired
    MemberEventRepository memberEventRepository;
    @Autowired
    ReactionRepository reactionRepository;
    @Autowired
    StatusRepository statusRepository;
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
        RestAssuredMockMvc.webAppContextSetup(context);

        chatRepository.deleteAll();
        messageRepository.deleteAll();
        memberEventRepository.deleteAll();
        reactionRepository.deleteAll();
        statusRepository.deleteAll();

        Chat chat = createChat("test", CHAT_ID);
        createAddMemberEvent(chat, USER_ID_1);
        createAddMemberEvent(chat, USER_ID_2);
        createMessage(chat, MESSAGE_ID_1, USER_ID_1);
        createMessage(chat, MESSAGE_ID_2, UUID.randomUUID());
        createReaction(MESSAGE_ID_1, USER_ID_1);

        when(userServiceClient.doesIdExist(any())).thenReturn(true);
        when(userServiceClient.doIdsExist(any())).thenReturn(true);
        when(contactServiceClient.areUsersInContactList(any())).thenReturn(true);

        doNothing().when(wsServiceClient).sendChatNewEvent(any());
        doNothing().when(wsServiceClient).sendChatUpdateEvent(any());
        doNothing().when(wsServiceClient).sendChatLastMessageEvent(any());
        doNothing().when(wsServiceClient).sendChatLastMessageUpdateEvent(any());
        doNothing().when(wsServiceClient).sendMessageNewEvent(any());
        doNothing().when(wsServiceClient).sendMessageUpdateEvent(any());
        doNothing().when(wsServiceClient).sendStatusesEvent(any());
        doNothing().when(wsServiceClient).sendReactionsEvent(any());
        doNothing().when(eventServiceClient).addChatEvent(any());
        doNothing().when(eventServiceClient).deleteChatEventsForUser(any());
    }

    private Chat createChat(String title, UUID chatId) {
        Chat chat = TestChat.defaultBuilder().id(chatId).title(title).build().toParent();
        return entityManager.merge(chat);
    }

    private void createAddMemberEvent(Chat chat, UUID userId) {
        MemberEvent memberEvent = TestMemberEvent.defaultBuilder()
                .chat(chat)
                .userId(userId)
                .type(MemberEventType.ADD_MEMBER)
                .build()
                .toParent();
        memberEventRepository.saveAndFlush(memberEvent);
        entityManager.refresh(chat);
    }

    private void createMessage(Chat chat, UUID messageId, UUID userId) {
        Message message = TestMessage.defaultBuilder()
                .chat(chat)
                .id(messageId)
                .userId(userId)
                .text("test")
                .build()
                .toParent();
        entityManager.merge(message);
        entityManager.refresh(chat);
    }

    private void createReaction(UUID messageId, UUID userId) {
        Reaction reaction = TestReaction.defaultBuilder()
                .messageId(messageId)
                .userId(userId)
                .type(ReactionType.LIKE)
                .build()
                .toParent();
        reactionRepository.saveAndFlush(reaction);
    }

}
