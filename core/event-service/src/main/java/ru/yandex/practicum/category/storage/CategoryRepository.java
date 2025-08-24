package ru.yandex.practicum.category.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.category.model.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
