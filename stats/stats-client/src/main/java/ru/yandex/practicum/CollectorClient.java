package ru.yandex.practicum;

import com.google.protobuf.Timestamp;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.stats.action.ActionTypeProto;
import ru.yandex.practicum.grpc.stats.action.UserActionProto;
import ru.yandex.practicum.grpc.stats.collector.UserActionControllerGrpc;

import java.time.Instant;

@Component
public class CollectorClient {
    @GrpcClient("collector")
    private UserActionControllerGrpc.UserActionControllerBlockingStub collectorServer;

    public void collectUserAction(Long userId, Long eventId, String actionType, Instant timestamp) {
        final UserActionProto request = UserActionProto.newBuilder()
                .setUserId(userId)
                .setEventId(eventId)
                .setActionType(ActionTypeProto.valueOf(actionType))
                .setTimestamp(
                        Timestamp.newBuilder()
                        .setSeconds(timestamp.getEpochSecond())
                        .setNanos(timestamp.getNano())
                        .build()
                ).build();
        collectorServer.collectUserAction(request);
    }
}
