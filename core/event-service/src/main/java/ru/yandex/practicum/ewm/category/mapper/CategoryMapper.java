package ru.yandex.practicum.ewm.category.mapper;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.ewm.category.dto.CategoryCreateDto;
import ru.yandex.practicum.ewm.category.dto.CategoryRequestDto;
import ru.yandex.practicum.ewm.category.model.Category;

@Component
public class CategoryMapper {
    public static Category toEntity(CategoryCreateDto categoryCreateDto) {
        Category category = new Category();
        category.setName(categoryCreateDto.getName());

        return category;
    }

    public static CategoryRequestDto toRequestDto(Category category) {
        return new CategoryRequestDto(
                category.getId(),
                category.getName()
        );
    }
}
