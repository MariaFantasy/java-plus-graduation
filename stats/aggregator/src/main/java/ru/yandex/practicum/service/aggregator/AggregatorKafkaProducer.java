package ru.yandex.practicum.service.aggregator;

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
public class AggregatorKafkaProducer {
    private final Producer<String, SpecificRecordBase> producer;

    public AggregatorKafkaProducer(KafkaProperties kafkaProperties) {
        Properties config = new Properties();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServers());
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, kafkaProperties.getKeySerializerClass());
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, kafkaProperties.getValueSerializerClass());
        config.put(ProducerConfig.CLIENT_ID_CONFIG, kafkaProperties.getAggregatorProducerClient());
        producer = new KafkaProducer<>(config);
    }

    public void send(SpecificRecordBase event, String topic) {
        ProducerRecord<String, SpecificRecordBase> record = new ProducerRecord<>(topic, event);
        producer.send(record);
        log.info("В кафку {} отправлено событие с телом {}", topic, event);
    }

    public void flush() {
        producer.flush();
    }

    public void close() {
        producer.close();
    }
}
