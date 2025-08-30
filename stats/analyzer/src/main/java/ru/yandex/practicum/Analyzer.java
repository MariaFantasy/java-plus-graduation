package ru.yandex.practicum;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ConfigurableApplicationContext;
import ru.yandex.practicum.controller.event_similarity.EventSimilarityStarter;
import ru.yandex.practicum.controller.user_action.UserActionStarter;

@SpringBootApplication
@EnableConfigurationProperties(KafkaProperties.class)
public class Analyzer {
    public static void main(String[] args) {
        // Запуск Spring Boot приложения при помощи вспомогательного класса SpringApplication
        // метод run возвращает назад настроенный контекст, который мы можем использовать для
        // получения настроенных бинов
        ConfigurableApplicationContext context = SpringApplication.run(Analyzer.class, args);

        // Получаем бины читающих из кафки из контекста и запускаем
        final UserActionStarter userActionStarter = context.getBean(UserActionStarter.class);
        final EventSimilarityStarter eventSimilarityStarter = context.getBean(EventSimilarityStarter.class);

        Thread userActionStarterThread = new Thread(userActionStarter);
        userActionStarterThread.setName("UserAction");
        userActionStarterThread.start();

        Thread eventSimilarityStarterThread = new Thread(eventSimilarityStarter);
        eventSimilarityStarterThread.setName("EventSimilarity");
        eventSimilarityStarterThread.start();
    }
}
