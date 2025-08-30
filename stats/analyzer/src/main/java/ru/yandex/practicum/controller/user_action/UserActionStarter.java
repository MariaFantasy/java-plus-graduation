package ru.yandex.practicum.controller.user_action;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.KafkaProperties;
import ru.yandex.practicum.ewm.stats.avro.UserActionAvro;
import ru.yandex.practicum.service.user_action.UserActionService;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserActionStarter implements Runnable {
    private final KafkaProperties kafkaProperties;
    private final UserActionKafkaConsumer consumer;
    private final UserActionService service;

    @Override
    public void run() {
        try {
            consumer.subscribe(kafkaProperties.getAction().getTopic());

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

    public void handleRecord(UserActionAvro avro) {
        service.saveUserAction(avro);
    }
}
