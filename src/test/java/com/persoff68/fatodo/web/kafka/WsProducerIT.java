package com.persoff68.fatodo.web.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.persoff68.fatodo.builder.TestChat;
import com.persoff68.fatodo.builder.TestMessage;
import com.persoff68.fatodo.client.UserServiceClient;
import com.persoff68.fatodo.client.WsServiceClient;
import com.persoff68.fatodo.config.util.KafkaUtils;
import com.persoff68.fatodo.model.Chat;
import com.persoff68.fatodo.model.MemberEvent;
import com.persoff68.fatodo.model.Message;
import com.persoff68.fatodo.model.constant.EventMessageType;
import com.persoff68.fatodo.model.constant.MemberEventType;
import com.persoff68.fatodo.repository.ChatRepository;
import com.persoff68.fatodo.repository.MemberEventRepository;
import com.persoff68.fatodo.repository.MessageRepository;
import com.persoff68.fatodo.service.ChatService;
import com.persoff68.fatodo.service.MessageService;
import com.persoff68.fatodo.service.ReactionService;
import com.persoff68.fatodo.service.StatusService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.ContainerTestUtils;
import org.springframework.test.annotation.DirtiesContext;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest(properties = {
        "kafka.bootstrapAddress=PLAINTEXT://localhost:9092",
        "kafka.groupId=test",
        "kafka.partitions=1",
        "kafka.autoOffsetResetConfig=earliest"
})
@DirtiesContext
@EmbeddedKafka(partitions = 1, brokerProperties = {"listeners=PLAINTEXT://localhost:9092", "port=9092"})
public class WsProducerIT {

    private static final String USER_ID_1 = "3c300277-b5ea-48d1-80db-ead620cf5846";
    private static final String USER_ID_2 = "357a2a99-7b7e-4336-9cd7-18f2cf73fab9";
    private static final String USER_ID_3 = "a762e074-0c26-4a3e-9495-44ccb2baf85c";

    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;

    @Autowired
    ChatService chatService;
    @Autowired
    MessageService messageService;
    @Autowired
    StatusService statusService;
    @Autowired
    ReactionService reactionService;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    ChatRepository chatRepository;
    @Autowired
    MessageRepository messageRepository;
    @Autowired
    MemberEventRepository memberEventRepository;

    @MockBean
    UserServiceClient userServiceClient;

    @SpyBean
    WsServiceClient wsServiceClient;

    private ConcurrentMessageListenerContainer<String, String> wsContainer;
    private BlockingQueue<ConsumerRecord<String, String>> wsRecords;

    private Chat chat;
    private Message message;

    @BeforeEach
    void setup() throws Exception {
        when(userServiceClient.doIdsExist(any())).thenReturn(true);

        chat = createChat("test_chat", false, USER_ID_1, USER_ID_2);
        message = createMessage(chat, USER_ID_1);

        startWsConsumer();
    }

    @AfterEach
    void cleanup() {
        chatRepository.deleteAll();
        messageRepository.deleteAll();
        memberEventRepository.deleteAll();

        stopWsConsumer();
    }

    @Test
    void testSendChatNewEvent() throws Exception {
        chatService.createDirect(UUID.fromString(USER_ID_1), UUID.fromString(USER_ID_3));

        ConsumerRecord<String, String> record = wsRecords.poll(10, TimeUnit.SECONDS);

        assertThat(wsServiceClient instanceof WsProducer).isTrue();
        assertThat(record).isNotNull();
        assertThat(record.key()).isEqualTo("new");
        verify(wsServiceClient).sendChatNewEvent(any());
    }

    @Test
    void testSendChatUpdateEvent() throws Exception {
        chatService.rename(UUID.fromString(USER_ID_1), chat.getId(), "new_test");

        ConsumerRecord<String, String> record = wsRecords.poll(10, TimeUnit.SECONDS);

        assertThat(wsServiceClient instanceof WsProducer).isTrue();
        assertThat(record).isNotNull();
        assertThat(record.key()).isEqualTo("update");
        verify(wsServiceClient).sendChatUpdateEvent(any());
    }

    @Test
    void testSendChatLastMessageEvent() throws Exception {
        messageService.send(UUID.fromString(USER_ID_1), chat.getId(), "message", null);

        wsRecords.poll(10, TimeUnit.SECONDS);
        ConsumerRecord<String, String> record = wsRecords.poll(10, TimeUnit.SECONDS);

        assertThat(wsServiceClient instanceof WsProducer).isTrue();
        assertThat(record).isNotNull();
        assertThat(record.key()).isEqualTo("last-message-new");
        verify(wsServiceClient).sendChatLastMessageEvent(any());
    }

    @Test
    void testSendChatLastMessageUpdateEvent() throws Exception {
        messageService.edit(UUID.fromString(USER_ID_1), message.getId(), "updated-message");

        wsRecords.poll(10, TimeUnit.SECONDS);
        ConsumerRecord<String, String> record = wsRecords.poll(10, TimeUnit.SECONDS);

        assertThat(wsServiceClient instanceof WsProducer).isTrue();
        assertThat(record).isNotNull();
        assertThat(record.key()).isEqualTo("last-message-update");
        verify(wsServiceClient).sendChatLastMessageUpdateEvent(any());
    }

    @Test
    void testSendMessageNewEvent() throws Exception {
        messageService.send(UUID.fromString(USER_ID_1), chat.getId(), "message", null);

        ConsumerRecord<String, String> record = wsRecords.poll(10, TimeUnit.SECONDS);

        assertThat(wsServiceClient instanceof WsProducer).isTrue();
        assertThat(record).isNotNull();
        assertThat(record.key()).isEqualTo("message-new");
        verify(wsServiceClient).sendMessageNewEvent(any());
    }

    @Test
    void testSendMessageUpdateEvent() throws Exception {
        messageService.edit(UUID.fromString(USER_ID_1), message.getId(), "updated-message");

        ConsumerRecord<String, String> record = wsRecords.poll(10, TimeUnit.SECONDS);

        assertThat(wsServiceClient instanceof WsProducer).isTrue();
        assertThat(record).isNotNull();
        assertThat(record.key()).isEqualTo("message-update");
        verify(wsServiceClient).sendMessageUpdateEvent(any());
    }

    @Test
    void testSendStatusesEvent() throws Exception {
        statusService.markAsRead(UUID.fromString(USER_ID_2), message.getId());

        ConsumerRecord<String, String> record = wsRecords.poll(10, TimeUnit.SECONDS);

        assertThat(wsServiceClient instanceof WsProducer).isTrue();
        assertThat(record).isNotNull();
        assertThat(record.key()).isEqualTo("statuses");
        verify(wsServiceClient).sendStatusesEvent(any());
    }

    @Test
    void testSendReactionsEvent() throws Exception {
        reactionService.setLike(UUID.fromString(USER_ID_2), message.getId());

        ConsumerRecord<String, String> record = wsRecords.poll(10, TimeUnit.SECONDS);

        assertThat(wsServiceClient instanceof WsProducer).isTrue();
        assertThat(record).isNotNull();
        assertThat(record.key()).isEqualTo("reactions");
        verify(wsServiceClient).sendReactionsEvent(any());
    }

    private void startWsConsumer() {
        ConcurrentKafkaListenerContainerFactory<String, String> stringContainerFactory =
                KafkaUtils.buildStringContainerFactory(embeddedKafkaBroker.getBrokersAsString(), "test", "earliest");
        wsContainer = stringContainerFactory.createContainer("ws_chat");
        wsRecords = new LinkedBlockingQueue<>();
        wsContainer.setupMessageListener((MessageListener<String, String>) wsRecords::add);
        wsContainer.start();
        ContainerTestUtils.waitForAssignment(wsContainer, embeddedKafkaBroker.getPartitionsPerTopic());
    }

    private void stopWsConsumer() {
        wsContainer.stop();
    }

    private Chat createChat(String title, boolean isDirect, String... userIds) throws Exception {
        Chat chat = TestChat.defaultBuilder().title(title).isDirect(isDirect).build().toParent();
        Chat savedChat = chatRepository.saveAndFlush(chat);
        createAddMemberEvents(savedChat, userIds);
        createEmptyMessage(savedChat, UUID.fromString(userIds[0]));
        return savedChat;
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

    private Message createMessage(Chat chat, String userId) {
        Message message = TestMessage.defaultBuilder()
                .chat(chat).userId(UUID.fromString(userId)).build().toParent();
        return messageRepository.saveAndFlush(message);
    }

}
