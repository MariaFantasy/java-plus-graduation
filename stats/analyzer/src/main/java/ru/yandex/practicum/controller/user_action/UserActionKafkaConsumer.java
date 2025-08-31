package ru.yandex.practicum.controller.user_action;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.TopicPartition;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.KafkaProperties;
import ru.yandex.practicum.ewm.stats.avro.UserActionAvro;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.function.Consumer;

@Slf4j
@Service
public class UserActionKafkaConsumer {
    private final KafkaConsumer<String, UserActionAvro> consumer;
    private static final Map<TopicPartition, OffsetAndMetadata> currentOffsets = new HashMap<>();

    public UserActionKafkaConsumer(KafkaProperties kafkaProperties) {
        Properties config = new Properties();
        log.info("Connection to Kafka: BOOTSTRAP_SERVERS_CONFIG={}, KEY_DESERIALIZER_CLASS_CONFIG={}, VALUE_DESERIALIZER_CLASS_CONFIG={}, CLIENT_ID_CONFIG={}, GROUP_ID_CONFIG={}",
                kafkaProperties.getBootstrapServers(),
                kafkaProperties.getKeyDeserializerClass(),
                kafkaProperties.getAction().getValueDeserializerClass(),
                kafkaProperties.getAction().getConsumerClient(),
                kafkaProperties.getAction().getConsumerGroup()
        );
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServers());
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, kafkaProperties.getKeyDeserializerClass());
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, kafkaProperties.getAction().getValueDeserializerClass());
        config.put(ConsumerConfig.CLIENT_ID_CONFIG, kafkaProperties.getAction().getConsumerClient());
        config.put(ConsumerConfig.GROUP_ID_CONFIG, kafkaProperties.getAction().getConsumerGroup());
        consumer = new KafkaConsumer<>(config);
    }

    public void subscribe(String topic) {
        List<String> topics = List.of(topic);
        consumer.subscribe(topics);
    }

    public void read(Consumer<UserActionAvro> handleRecord) {
        ConsumerRecords<String, UserActionAvro> records = consumer.poll(Duration.ofMillis(1000));
        int count = 0;
        for (ConsumerRecord<String, UserActionAvro> record : records) {
            handleRecord.accept(record.value());
            manageOffsets(record, count, consumer);
            count++;
        }
        consumer.commitAsync();
    }

    private static void manageOffsets(ConsumerRecord<String, UserActionAvro> record, int count, KafkaConsumer<String, UserActionAvro> consumer) {
        // обновляем текущий оффсет для топика-партиции
        currentOffsets.put(
                new TopicPartition(record.topic(), record.partition()),
                new OffsetAndMetadata(record.offset() + 1)
        );

        if(count % 10 == 0) {
            consumer.commitAsync(currentOffsets, (offsets, exception) -> {
                if(exception != null) {
                    log.warn("Ошибка во время фиксации оффсетов: {}", offsets, exception);
                }
            });
        }
    }

    public void commit() {
        consumer.commitAsync();
    }

    public void close() {
        consumer.close();
    }
}
