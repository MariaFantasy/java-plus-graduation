package ru.yandex.practicum.compilation.service;

import ru.yandex.practicum.dto.CompilationDto;
import ru.yandex.practicum.dto.NewCompilationDto;
import ru.yandex.practicum.dto.UpdateCompilationRequest;

import java.util.List;

public interface CompilationService {

    CompilationDto addCompilation(NewCompilationDto newCompilationDto);

    CompilationDto updateCompilation(Long compId, UpdateCompilationRequest updateCompilationRequest);

    void deleteCompilation(Long id);

    List<CompilationDto> getAllCompilations(Integer from, Integer size, Boolean pinned);

    CompilationDto findCompilationById(Long compId);
}
