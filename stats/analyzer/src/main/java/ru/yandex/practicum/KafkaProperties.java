package ru.yandex.practicum;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "kafka")
@Data
public class KafkaProperties {
    private String bootstrapServers;
    private String keyDeserializerClass;
    private Action action;
    private Similarity similarity;

    @Data
    public static class Action {
        private String valueDeserializerClass;
        private String consumerClient;
        private String consumerGroup;
        private String topic;
    }

    @Data
    public static class Similarity {
        private String valueDeserializerClass;
        private String consumerClient;
        private String consumerGroup;
        private String topic;
    }
}
