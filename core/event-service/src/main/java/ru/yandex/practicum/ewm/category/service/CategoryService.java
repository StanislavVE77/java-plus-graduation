package ru.yandex.practicum.ewm.category.service;

import ru.yandex.practicum.ewm.category.dto.CategoryCreateDto;
import ru.yandex.practicum.ewm.category.dto.CategoryRequestDto;

import java.util.List;

public interface CategoryService {
    CategoryRequestDto create(CategoryCreateDto categoryCreateDto);

    CategoryRequestDto update(CategoryCreateDto categoryCreateDto, Long catId);

    void delete(Long catId);

    List<CategoryRequestDto> get(int from, int size);

    CategoryRequestDto getById(Long catId);

}
