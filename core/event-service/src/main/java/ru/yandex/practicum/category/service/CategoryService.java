package ru.yandex.practicum.category.service;

import ru.yandex.practicum.dto.CategoryDto;
import ru.yandex.practicum.dto.NewCategoryDto;

import java.util.Collection;

public interface CategoryService {

    CategoryDto create(NewCategoryDto newCategoryDto);

    Collection<CategoryDto> findAll(Integer from, Integer size);

    CategoryDto findById(Long categoryId);

    CategoryDto update(Long categoryId, CategoryDto categoryDto);

    void delete(Long categoryId);
}
