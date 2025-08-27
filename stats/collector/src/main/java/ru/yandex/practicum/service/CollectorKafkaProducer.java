package ru.yandex.practicum.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.KafkaProperties;

import java.util.Properties;

@Slf4j
@Service
public class CollectorKafkaProducer {
    private final Producer<String, SpecificRecordBase> producer;

    public CollectorKafkaProducer(KafkaProperties kafkaProperties) {
        Properties config = new Properties();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServers());
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, kafkaProperties.getKeySerializerClass());
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, kafkaProperties.getValueSerializerClass());
        producer = new KafkaProducer<>(config);
    }

    public void send(SpecificRecordBase event, String topic) {
        ProducerRecord<String, SpecificRecordBase> record = new ProducerRecord<>(topic, event);
        log.info("В кафку {} отправлено событие с телом {}", topic, event);
        producer.send(record);
    }

    public void close() {
        producer.close();
    }
}