package ru.yandex.practicum.service.recommendation;

import ru.yandex.practicum.grpc.stats.event_recommendations.InteractionsCountRequestProto;
import ru.yandex.practicum.grpc.stats.event_recommendations.RecommendedEventProto;
import ru.yandex.practicum.grpc.stats.event_recommendations.SimilarEventsRequestProto;
import ru.yandex.practicum.grpc.stats.event_recommendations.UserPredictionsRequestProto;

import java.util.List;

public interface RecommendationService {

    List<RecommendedEventProto> getRecommendationsForUser(UserPredictionsRequestProto request);

    List<RecommendedEventProto> getSimilarEvents(SimilarEventsRequestProto request);

    List<RecommendedEventProto> getInteractionsCount(InteractionsCountRequestProto request);

}
