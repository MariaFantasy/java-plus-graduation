package ru.yandex.practicum.controller.event_similarity;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.KafkaProperties;
import ru.yandex.practicum.ewm.stats.avro.EventSimilarityAvro;
import ru.yandex.practicum.service.event_similarity.EventSimilarityService;

@Slf4j
@Component
@RequiredArgsConstructor
public class EventSimilarityStarter implements Runnable {
    private final KafkaProperties kafkaProperties;
    private final EventSimilarityKafkaConsumer consumer;
    private final EventSimilarityService service;

    @Override
    public void run() {
        try {
            consumer.subscribe(kafkaProperties.getSimilarity().getTopic());

            while (true) {
                consumer.read(this::handleRecord);
            }

        } catch (WakeupException ignored) {
            // игнорируем - закрываем консьюмер и продюсер в блоке finally
            log.info("Чтение топика {} остановлено.", kafkaProperties.getAction().getTopic());
        } catch (Exception e) {
            log.error("Ошибка во время обработки событий от датчиков", e);
        } finally {
            try {
                consumer.commit();

            } finally {
                log.info("Закрываем консьюмер");
                consumer.close();
            }
        }
    }

    public void handleRecord(EventSimilarityAvro avro) {
        service.saveEventSimilarity(avro);
    }
}
