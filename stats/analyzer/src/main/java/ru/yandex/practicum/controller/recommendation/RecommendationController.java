package ru.yandex.practicum.controller.recommendation;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import ru.yandex.practicum.stats.service.dashboard.RecommendationsControllerGrpc;
import ru.yandex.practicum.grpc.stats.event_recommendations.InteractionsCountRequestProto;
import ru.yandex.practicum.grpc.stats.event_recommendations.RecommendedEventProto;
import ru.yandex.practicum.grpc.stats.event_recommendations.SimilarEventsRequestProto;
import ru.yandex.practicum.grpc.stats.event_recommendations.UserPredictionsRequestProto;
import ru.yandex.practicum.service.recommendation.RecommendationService;

import java.util.List;

@Slf4j
@GrpcService
@RequiredArgsConstructor
public class RecommendationController extends RecommendationsControllerGrpc.RecommendationsControllerImplBase {
    final RecommendationService recommendationService;

    @Override
    public void getRecommendationsForUser(UserPredictionsRequestProto request, StreamObserver<RecommendedEventProto> responseObserver) {
        try {
            log.info("User Predictions Request received {}", request);
            final List<RecommendedEventProto> recommendedEvents = recommendationService.getRecommendationsForUser(request);
            log.info("User Predictions Request send response {}", recommendedEvents);
            recommendedEvents.forEach(responseObserver::onNext);
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(new StatusRuntimeException(
                    Status.INTERNAL
                            .withDescription(e.getLocalizedMessage())
                            .withCause(e)
            ));
        }
    }

    @Override
    public void getSimilarEvents(SimilarEventsRequestProto request, StreamObserver<RecommendedEventProto> responseObserver) {
        try {
            log.info("Similar Events Request received {}", request);
            final List<RecommendedEventProto> recommendedEvents = recommendationService.getSimilarEvents(request);
            log.info("Similar Events Request send response {}", recommendedEvents);
            recommendedEvents.forEach(responseObserver::onNext);
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(new StatusRuntimeException(
                    Status.INTERNAL
                            .withDescription(e.getLocalizedMessage())
                            .withCause(e)
            ));
        }
    }

    @Override
    public void getInteractionsCount(InteractionsCountRequestProto request, StreamObserver<RecommendedEventProto> responseObserver) {
        try {
            log.info("Interactions Count Request received {}", request);
            final List<RecommendedEventProto> recommendedEvents = recommendationService.getInteractionsCount(request);
            log.info("Interactions Count Request {}", recommendedEvents);
            recommendedEvents.forEach(responseObserver::onNext);
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(new StatusRuntimeException(
                    Status.INTERNAL
                            .withDescription(e.getLocalizedMessage())
                            .withCause(e)
            ));
        }
    }
}
