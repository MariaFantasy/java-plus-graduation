package ru.yandex.practicum;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "kafka")
@Data
public class KafkaProperties {
    private String bootstrapServers;
    private String keySerializerClass;
    private String valueSerializerClass;
    private Topic topic;

    @Data
    public static class Topic {
        private String sensor;
        private String hub;
    }
}
