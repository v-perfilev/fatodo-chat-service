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
import com.persoff68.fatodo.repository.ReactionRepository;
import com.persoff68.fatodo.repository.StatusRepository;
import com.persoff68.fatodo.service.ChatService;
import com.persoff68.fatodo.service.MemberEventService;
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

import java.util.ArrayList;
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
        "kafka.bootstrapAddress=localhost:9092",
        "kafka.groupId=test",
        "kafka.partitions=1",
        "kafka.autoOffsetResetConfig=earliest"
})
@DirtiesContext
@EmbeddedKafka(partitions = 1, brokerProperties = {"listeners=PLAINTEXT://localhost:9092", "port=9092"})
class WsProducerIT {

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
    MemberEventService memberEventService;
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
    @Autowired
    StatusRepository statusRepository;
    @Autowired
    ReactionRepository reactionRepository;

    @MockBean
    UserServiceClient userServiceClient;

    @SpyBean
    WsServiceClient wsServiceClient;

    private ConcurrentMessageListenerContainer<String, String> wsChatContainer;
    private BlockingQueue<ConsumerRecord<String, String>> wsChatRecords;

    private Chat chat;
    private Message message;

    @BeforeEach
    void setup() throws Exception {
        when(userServiceClient.doIdsExist(any())).thenReturn(true);

        chat = createChat("test_chat", false, USER_ID_1, USER_ID_2);
        message = createMessage(chat, USER_ID_1);

        startWsChatConsumer();
    }

    @AfterEach
    void cleanup() {
        chatRepository.deleteAll();
        messageRepository.deleteAll();
        memberEventRepository.deleteAll();
        statusRepository.deleteAll();
        reactionRepository.deleteAll();

        stopWsChatConsumer();
    }

    @Test
    void testSendChatNewEvent() throws Exception {
        chatService.createDirect(UUID.fromString(USER_ID_1), UUID.fromString(USER_ID_3));

        List<ConsumerRecord<String, String>> recordList = new ArrayList<>();
        List<String> recordKeyList = new ArrayList<>();
        waitForMultipleRecords(wsChatRecords, recordList, recordKeyList);

        assertThat(wsServiceClient).isInstanceOf(WsProducer.class);
        assertThat(recordList).isNotNull().isNotEmpty();
        assertThat(recordKeyList).contains("new");
        verify(wsServiceClient).sendChatNewEvent(any());
    }

    @Test
    void testSendChatUpdateEvent() throws Exception {
        chatService.rename(UUID.fromString(USER_ID_1), chat.getId(), "new_test");

        List<ConsumerRecord<String, String>> recordList = new ArrayList<>();
        List<String> recordKeyList = new ArrayList<>();
        waitForMultipleRecords(wsChatRecords, recordList, recordKeyList);

        assertThat(wsServiceClient).isInstanceOf(WsProducer.class);
        assertThat(recordList).isNotNull().isNotEmpty();
        assertThat(recordKeyList).contains("update");
        verify(wsServiceClient).sendChatUpdateEvent(any());
    }

    @Test
    void testSendChatDeleteEvent() throws Exception {
        memberEventService.leaveChat(UUID.fromString(USER_ID_2), chat.getId());

        List<ConsumerRecord<String, String>> recordList = new ArrayList<>();
        List<String> recordKeyList = new ArrayList<>();
        waitForMultipleRecords(wsChatRecords, recordList, recordKeyList);

        assertThat(wsServiceClient).isInstanceOf(WsProducer.class);
        assertThat(recordList).isNotNull().isNotEmpty();
        assertThat(recordKeyList).contains("delete");
        verify(wsServiceClient).sendChatDeleteEvent(any());
    }

    @Test
    void testSendChatLastMessageEvent() throws Exception {
        messageService.send(UUID.fromString(USER_ID_1), chat.getId(), "message", null);

        List<ConsumerRecord<String, String>> recordList = new ArrayList<>();
        List<String> recordKeyList = new ArrayList<>();
        waitForMultipleRecords(wsChatRecords, recordList, recordKeyList);

        assertThat(wsServiceClient).isInstanceOf(WsProducer.class);
        assertThat(recordList).isNotNull().isNotEmpty();
        assertThat(recordKeyList).contains("last-message");
        verify(wsServiceClient).sendChatLastMessageEvent(any());
    }

    @Test
    void testSendChatLastMessageUpdateEvent() throws Exception {
        messageService.edit(UUID.fromString(USER_ID_1), message.getId(), "updated-message");

        List<ConsumerRecord<String, String>> recordList = new ArrayList<>();
        List<String> recordKeyList = new ArrayList<>();
        waitForMultipleRecords(wsChatRecords, recordList, recordKeyList);

        assertThat(wsServiceClient).isInstanceOf(WsProducer.class);
        assertThat(recordList).isNotNull().isNotEmpty();
        assertThat(recordKeyList).contains("last-message-update");
        verify(wsServiceClient).sendChatLastMessageUpdateEvent(any());
    }

    @Test
    void testSendMessageNewEvent() throws Exception {
        messageService.send(UUID.fromString(USER_ID_1), chat.getId(), "message", null);

        List<ConsumerRecord<String, String>> recordList = new ArrayList<>();
        List<String> recordKeyList = new ArrayList<>();
        waitForMultipleRecords(wsChatRecords, recordList, recordKeyList);

        assertThat(wsServiceClient).isInstanceOf(WsProducer.class);
        assertThat(recordList).isNotNull().isNotEmpty();
        assertThat(recordKeyList).contains("message-new");
        verify(wsServiceClient).sendMessageNewEvent(any());
    }

    @Test
    void testSendMessageUpdateEvent() throws Exception {
        messageService.edit(UUID.fromString(USER_ID_1), message.getId(), "updated-message");

        List<ConsumerRecord<String, String>> recordList = new ArrayList<>();
        List<String> recordKeyList = new ArrayList<>();
        waitForMultipleRecords(wsChatRecords, recordList, recordKeyList);

        assertThat(wsServiceClient).isInstanceOf(WsProducer.class);
        assertThat(recordList).isNotNull().isNotEmpty();
        assertThat(recordKeyList).contains("message-update");
        verify(wsServiceClient).sendMessageUpdateEvent(any());
    }

    @Test
    void testSendStatusesEvent() throws Exception {
        statusService.markAsRead(UUID.fromString(USER_ID_2), message.getId());

        List<ConsumerRecord<String, String>> recordList = new ArrayList<>();
        List<String> recordKeyList = new ArrayList<>();
        waitForMultipleRecords(wsChatRecords, recordList, recordKeyList);

        assertThat(wsServiceClient).isInstanceOf(WsProducer.class);
        assertThat(recordList).isNotNull().isNotEmpty();
        assertThat(recordKeyList).contains("statuses");
        verify(wsServiceClient).sendStatusesEvent(any());
    }

    @Test
    void testSendReactionsEvent() throws Exception {
        reactionService.setLike(UUID.fromString(USER_ID_2), message.getId());

        List<ConsumerRecord<String, String>> recordList = new ArrayList<>();
        List<String> recordKeyList = new ArrayList<>();
        waitForMultipleRecords(wsChatRecords, recordList, recordKeyList);

        assertThat(wsServiceClient).isInstanceOf(WsProducer.class);
        assertThat(recordList).isNotNull().isNotEmpty();
        assertThat(recordKeyList).contains("reactions");
        verify(wsServiceClient).sendReactionsEvent(any());
    }

    private void startWsChatConsumer() {
        ConcurrentKafkaListenerContainerFactory<String, String> stringContainerFactory =
                KafkaUtils.buildStringContainerFactory(embeddedKafkaBroker.getBrokersAsString(), "test", "earliest");
        wsChatContainer = stringContainerFactory.createContainer("ws_chat");
        wsChatRecords = new LinkedBlockingQueue<>();
        wsChatContainer.setupMessageListener((MessageListener<String, String>) wsChatRecords::add);
        wsChatContainer.start();
        ContainerTestUtils.waitForAssignment(wsChatContainer, embeddedKafkaBroker.getPartitionsPerTopic());
    }

    private void stopWsChatConsumer() {
        wsChatContainer.stop();
    }

    private void waitForMultipleRecords(BlockingQueue<ConsumerRecord<String, String>> records,
                                        List<ConsumerRecord<String, String>> recordList,
                                        List<String> recordKeyList) throws InterruptedException {
        ConsumerRecord<String, String> record;
        do {
            record = records.poll(1, TimeUnit.SECONDS);
            if (record != null) {
                recordList.add(record);
            }
        } while (record != null);
        List<String> keyList = recordList.stream().map(ConsumerRecord::key).toList();
        recordKeyList.addAll(keyList);
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
