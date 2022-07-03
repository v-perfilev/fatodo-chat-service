package com.persoff68.fatodo.config;

import com.persoff68.fatodo.config.annotation.ConditionalOnPropertyNotNull;
import com.persoff68.fatodo.config.util.KafkaUtils;
import com.persoff68.fatodo.model.dto.ChatDTO;
import com.persoff68.fatodo.model.dto.MessageDTO;
import com.persoff68.fatodo.model.dto.ReactionsDTO;
import com.persoff68.fatodo.model.dto.StatusesDTO;
import com.persoff68.fatodo.model.dto.WsEventDTO;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.core.KafkaTemplate;

@Configuration
@EnableKafka
@ConditionalOnPropertyNotNull(value = "kafka.bootstrapAddress")
public class KafkaConfiguration {

    @Value(value = "${kafka.bootstrapAddress}")
    private String bootstrapAddress;

    @Value(value = "${kafka.partitions}")
    private int partitions;

    @Bean
    public KafkaAdmin kafkaAdmin() {
        return KafkaUtils.buildKafkaAdmin(bootstrapAddress);
    }

    @Bean
    public NewTopic activationNewTopic() {
        return KafkaUtils.buildTopic("ws_chat", partitions);
    }

    @Bean
    public KafkaTemplate<String, WsEventDTO<ChatDTO>> wsEventChatKafkaTemplate() {
        return KafkaUtils.buildJsonKafkaTemplate(bootstrapAddress);
    }

    @Bean
    public KafkaTemplate<String, WsEventDTO<MessageDTO>> wsEventMessageKafkaTemplate() {
        return KafkaUtils.buildJsonKafkaTemplate(bootstrapAddress);
    }

    @Bean
    public KafkaTemplate<String, WsEventDTO<StatusesDTO>> wsEventStatusesKafkaTemplate() {
        return KafkaUtils.buildJsonKafkaTemplate(bootstrapAddress);
    }

    @Bean
    public KafkaTemplate<String, WsEventDTO<ReactionsDTO>> wsEventReactionsKafkaTemplate() {
        return KafkaUtils.buildJsonKafkaTemplate(bootstrapAddress);
    }

}
