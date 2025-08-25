package ru.yandex.practicum.compilation.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.yandex.practicum.compilation.model.Compilation;
import ru.yandex.practicum.dto.CompilationDto;
import ru.yandex.practicum.dto.EventShortDto;
import ru.yandex.practicum.dto.NewCompilationDto;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CompilationDtoMapper {

    @Mapping(target = "events", source = "eventsDto")
    CompilationDto toCompilationDto(Compilation compilation, List<EventShortDto> eventsDto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "events", ignore = true)
    Compilation toCompilation(NewCompilationDto compilationDto);
}
