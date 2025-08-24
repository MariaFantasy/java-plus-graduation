package ru.yandex.practicum.category.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.yandex.practicum.category.model.Category;
import ru.yandex.practicum.dto.CategoryDto;
import ru.yandex.practicum.dto.NewCategoryDto;

@Mapper(componentModel = "spring")
public interface CategoryDtoMapper {

    CategoryDto mapToDto(Category category);

    @Mapping(target = "id", ignore = true)
    Category mapFromDto(NewCategoryDto categoryDto);

    Category mapFromDto(CategoryDto categoryDto);
}
