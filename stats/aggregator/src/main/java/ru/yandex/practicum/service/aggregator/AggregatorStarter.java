package ru.yandex.practicum.service.aggregator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.KafkaProperties;
import ru.yandex.practicum.ewm.stats.avro.EventSimilarityAvro;
import ru.yandex.practicum.ewm.stats.avro.UserActionAvro;
import ru.yandex.practicum.service.similarity.SimilarityCalculatorService;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class AggregatorStarter {
    private final KafkaProperties kafkaProperties;
    private final AggregatorKafkaProducer producer;
    private final AggregatorKafkaConsumer consumer;
    private final SimilarityCalculatorService similarityCalculatorService;

    public void start() {
        try {
            log.info("Подписка на топик: {}", kafkaProperties.getTopic().getAction());
            consumer.subscribe(kafkaProperties.getTopic().getAction());
            log.info("Успешная подписка на топик: {}", kafkaProperties.getTopic().getAction());

            while (true) {
                consumer.read(this::handleRecord);
            }

        } catch (WakeupException ignored) {
            // игнорируем - закрываем консьюмер и продюсер в блоке finally
            log.info("Чтение топика {} остановлено.", kafkaProperties.getTopic().getAction());
        } catch (Exception e) {
            log.error("Ошибка во время обработки событий от датчиков", e);
        } finally {
            try {
                producer.flush();
                consumer.commit();
            } finally {
                log.info("Закрываем консьюмер");
                consumer.close();
                log.info("Закрываем продюсер");
                producer.close();
            }
        }
    }

    public void handleRecord(UserActionAvro avro) {
        List<EventSimilarityAvro> eventsSimilarity = similarityCalculatorService.getSimilarityEvent(avro);
        for (EventSimilarityAvro record: eventsSimilarity) {
            producer.send(record, kafkaProperties.getTopic().getSimilarity());
        }
        log.info("Отправляем в кафку событие схожести событий");
    }
}
