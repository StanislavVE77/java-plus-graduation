package ru.yandex.practicum.ewm.compilation.service;

import ru.yandex.practicum.ewm.compilation.dto.CompilationCreateDto;
import ru.yandex.practicum.ewm.compilation.dto.CompilationRequestDto;
import ru.yandex.practicum.ewm.compilation.dto.CompilationUpdateDto;

import java.util.List;

public interface CompilationService {

    CompilationRequestDto create(CompilationCreateDto compilationCreateDto);

    CompilationRequestDto update(CompilationUpdateDto compilationUpdateDto, Long compId);

    void delete(Long compId);

    List<CompilationRequestDto> get(Boolean pinned, int from, int size);

    CompilationRequestDto getById(Long compId);
}
