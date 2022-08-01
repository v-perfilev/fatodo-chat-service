package com.persoff68.fatodo.web.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.persoff68.fatodo.builder.TestChat;
import com.persoff68.fatodo.builder.TestMemberEvent;
import com.persoff68.fatodo.client.EventServiceClient;
import com.persoff68.fatodo.client.UserServiceClient;
import com.persoff68.fatodo.config.util.KafkaUtils;
import com.persoff68.fatodo.model.Chat;
import com.persoff68.fatodo.model.MemberEvent;
import com.persoff68.fatodo.repository.ChatRepository;
import com.persoff68.fatodo.repository.MemberEventRepository;
import com.persoff68.fatodo.service.ChatService;
import com.persoff68.fatodo.service.MemberEventService;
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
import java.util.List;
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
class EventProducerIT {

    private static final String USER_ID_1 = "3c300277-b5ea-48d1-80db-ead620cf5846";
    private static final String USER_ID_2 = "357a2a99-7b7e-4336-9cd7-18f2cf73fab9";
    private static final String USER_ID_3 = "a762e074-0c26-4a3e-9495-44ccb2baf85c";

    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;

    @Autowired
    ChatService chatService;
    @Autowired
    MemberEventService memberEventService;
    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    ChatRepository chatRepository;
    @Autowired
    MemberEventRepository memberEventRepository;

    @MockBean
    UserServiceClient userServiceClient;

    @SpyBean
    EventServiceClient eventServiceClient;

    private ConcurrentMessageListenerContainer<String, String> eventAddContainer;
    private BlockingQueue<ConsumerRecord<String, String>> eventAddRecords;

    private ConcurrentMessageListenerContainer<String, String> eventDeleteContainer;
    private BlockingQueue<ConsumerRecord<String, String>> eventDeleteRecords;

    Chat chat;

    @BeforeEach
    void setup() {
        chat = createIndirectChat(USER_ID_1, USER_ID_3);

        when(userServiceClient.doIdsExist(any())).thenReturn(true);


        startEventAddConsumer();
        startEventDeleteConsumer();
    }

    @AfterEach
    void cleanup() {
        chatRepository.deleteAll();
        memberEventRepository.deleteAll();

        stopEventAddConsumer();
        stopEventDeleteConsumer();
    }

    @Test
    void testSendChatEvent_ok() throws Exception {
        chatService.createDirect(UUID.fromString(USER_ID_1), UUID.fromString(USER_ID_2));

        ConsumerRecord<String, String> record = eventAddRecords.poll(5, TimeUnit.SECONDS);

        assertThat(eventServiceClient).isInstanceOf(EventProducer.class);
        assertThat(record).isNotNull();
        assertThat(record.key()).isEqualTo("chat");
        verify(eventServiceClient).addChatEvent(any());
    }

    @Test
    void testSendDeleteChatEventForUsers_ok() throws Exception {
        memberEventService.removeUsers(UUID.fromString(USER_ID_1), chat.getId(), List.of(UUID.fromString(USER_ID_3)));

        ConsumerRecord<String, String> record = eventDeleteRecords.poll(5, TimeUnit.SECONDS);

        assertThat(eventServiceClient).isInstanceOf(EventProducer.class);
        assertThat(record).isNotNull();
        assertThat(record.key()).isEqualTo("chat-delete-users");
        verify(eventServiceClient).deleteChatEventsForUser(any());
    }


    private void startEventAddConsumer() {
        ConcurrentKafkaListenerContainerFactory<String, String> stringContainerFactory =
                KafkaUtils.buildStringContainerFactory(embeddedKafkaBroker.getBrokersAsString(), "test", "earliest");
        eventAddContainer = stringContainerFactory.createContainer("event_add");
        eventAddRecords = new LinkedBlockingQueue<>();
        eventAddContainer.setupMessageListener((MessageListener<String, String>) eventAddRecords::add);
        eventAddContainer.start();
        ContainerTestUtils.waitForAssignment(eventAddContainer, embeddedKafkaBroker.getPartitionsPerTopic());
    }

    private void stopEventAddConsumer() {
        eventAddContainer.stop();
    }

    private void startEventDeleteConsumer() {
        ConcurrentKafkaListenerContainerFactory<String, String> stringContainerFactory =
                KafkaUtils.buildStringContainerFactory(embeddedKafkaBroker.getBrokersAsString(), "test", "earliest");
        eventDeleteContainer = stringContainerFactory.createContainer("event_delete");
        eventDeleteRecords = new LinkedBlockingQueue<>();
        eventDeleteContainer.setupMessageListener((MessageListener<String, String>) eventDeleteRecords::add);
        eventDeleteContainer.start();
        ContainerTestUtils.waitForAssignment(eventDeleteContainer, embeddedKafkaBroker.getPartitionsPerTopic());
    }

    private void stopEventDeleteConsumer() {
        eventDeleteContainer.stop();
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
