package com.persoff68.fatodo.web.kafka;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.persoff68.fatodo.client.UserServiceClient;
import com.persoff68.fatodo.client.WsServiceClient;
import com.persoff68.fatodo.config.util.KafkaUtils;
import com.persoff68.fatodo.model.dto.WsEventWithUsersDTO;
import com.persoff68.fatodo.repository.ChatRepository;
import com.persoff68.fatodo.repository.MemberEventRepository;
import com.persoff68.fatodo.repository.MessageRepository;
import com.persoff68.fatodo.service.ChatService;
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

    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;

    @Autowired
    ChatService chatService;

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

    private ConcurrentMessageListenerContainer<String, WsEventWithUsersDTO> wsContainer;
    private BlockingQueue<ConsumerRecord<String, WsEventWithUsersDTO>> wsRecords;

    @BeforeEach
    void setup() {
        when(userServiceClient.doIdsExist(any())).thenReturn(true);

        startWsChatConsumer();
    }

    @AfterEach
    void cleanup() {
        chatRepository.deleteAll();
        memberEventRepository.deleteAll();

        stopWsChatConsumer();
    }

    @Test
    void testSendChatNewEvent() throws Exception {
        chatService.createDirect(UUID.randomUUID(), UUID.randomUUID());

        ConsumerRecord<String, WsEventWithUsersDTO> record = wsRecords.poll(5, TimeUnit.SECONDS);

        assertThat(wsServiceClient).isInstanceOf(WsProducer.class);
        assertThat(record).isNotNull();
        verify(wsServiceClient).sendEvent(any());
    }

    private void startWsChatConsumer() {
        JavaType javaType = objectMapper.getTypeFactory().constructType(WsEventWithUsersDTO.class);
        ConcurrentKafkaListenerContainerFactory<String, WsEventWithUsersDTO> stringContainerFactory =
                KafkaUtils.buildJsonContainerFactory(embeddedKafkaBroker.getBrokersAsString(),
                        "test", "earliest", javaType);
        wsContainer = stringContainerFactory.createContainer("ws");
        wsRecords = new LinkedBlockingQueue<>();
        wsContainer.setupMessageListener((MessageListener<String, WsEventWithUsersDTO>) wsRecords::add);
        wsContainer.start();
        ContainerTestUtils.waitForAssignment(wsContainer, embeddedKafkaBroker.getPartitionsPerTopic());
    }

    private void stopWsChatConsumer() {
        wsContainer.stop();
    }

}
