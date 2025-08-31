package ru.yandex.practicum;

import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.stats.analyzer.RecommendationsControllerGrpc;
import ru.yandex.practicum.grpc.stats.event_recommendations.InteractionsCountRequestProto;
import ru.yandex.practicum.grpc.stats.event_recommendations.RecommendedEventProto;
import ru.yandex.practicum.grpc.stats.event_recommendations.SimilarEventsRequestProto;
import ru.yandex.practicum.grpc.stats.event_recommendations.UserPredictionsRequestProto;

import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Component
public class AnalyzerClient {
    @GrpcClient("analyzer")
    private RecommendationsControllerGrpc.RecommendationsControllerBlockingStub analyzerServer;

    public Stream<RecommendedEventProto> getRecommendationsForUser(Long userId, Long maxResults) {
        final UserPredictionsRequestProto request = UserPredictionsRequestProto.newBuilder()
                .setUserId(userId)
                .setMaxResults(maxResults)
                .build();
        final Iterator<RecommendedEventProto> iterator = analyzerServer.getRecommendationsForUser(request);
        return asStream(iterator);
    }

    public Stream<RecommendedEventProto> getSimilarEvents(Long eventId, Long userId, Long maxResults) {
        final SimilarEventsRequestProto request = SimilarEventsRequestProto.newBuilder()
                .setEventId(eventId)
                .setUserId(userId)
                .setMaxResults(maxResults)
                .build();
        final Iterator<RecommendedEventProto> iterator = analyzerServer.getSimilarEvents(request);
        return asStream(iterator);
    }

    public Stream<RecommendedEventProto> getInteractionsCount(List<Long> eventIds) {
        final InteractionsCountRequestProto request = InteractionsCountRequestProto.newBuilder()
                .addAllEventId(eventIds)
                .build();
        final Iterator<RecommendedEventProto> iterator = analyzerServer.getInteractionsCount(request);
        return asStream(iterator);
    }

    private Stream<RecommendedEventProto> asStream(Iterator<RecommendedEventProto> iterator) {
        return StreamSupport.stream(
                Spliterators.spliteratorUnknownSize(iterator, Spliterator.ORDERED),
                false
        );
    }
}
