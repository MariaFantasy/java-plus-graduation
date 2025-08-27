package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.KafkaProperties;
import ru.yandex.practicum.grpc.stats.action.UserActionProto;
import ru.yandex.practicum.mapper.UserActionMapper;

@Slf4j
@Service("collectServiceImpl")
@RequiredArgsConstructor
public class CollectorServiceImpl implements CollectorService {
    private final CollectorKafkaProducer kafkaProducer;
    private final UserActionMapper userActionMapper;
    private final KafkaProperties kafkaProperties;

    @Override
    public void loadUserAction(UserActionProto userAction) {
        log.info("Start user action save {}", userAction);
        kafkaProducer.send(userActionMapper.mapToAvro(userAction), kafkaProperties.getTopic().getSensor());
        log.info("Success user action save {}", userAction);
    }
}
