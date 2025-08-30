package ru.yandex.practicum.mapper.model;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.yandex.practicum.ewm.stats.avro.EventSimilarityAvro;
import ru.yandex.practicum.model.EventSimilarity;

@Mapper(componentModel = "spring")
public interface EventSimilarityMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "eventAId", source = "eventA")
    @Mapping(target = "eventBId", source = "eventB")
    EventSimilarity mapFromAvro(EventSimilarityAvro eventSimilarityAvro);
}
