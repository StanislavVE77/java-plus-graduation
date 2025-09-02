package ru.yandex.practicum.ewm.compilation.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.ewm.compilation.dto.CompilationCreateDto;
import ru.yandex.practicum.ewm.compilation.dto.CompilationRequestDto;
import ru.yandex.practicum.ewm.compilation.dto.CompilationUpdateDto;
import ru.yandex.practicum.ewm.compilation.mapper.CompilationMapper;
import ru.yandex.practicum.ewm.compilation.model.Compilation;
import ru.yandex.practicum.ewm.compilation.storage.CompilationRepository;
import ru.yandex.practicum.ewm.event.model.Event;
import ru.yandex.practicum.ewm.event.storage.EventRepository;
import ru.yandex.practicum.ewm.exception.CompilationNotFoundException;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CompilationServiceImpl implements CompilationService {

    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;

    @Override
    public CompilationRequestDto create(CompilationCreateDto compilationCreateDto) {
        if (compilationCreateDto.getPinned() == null) {
            compilationCreateDto.setPinned(false);
        }

        List<Long> eventIds = compilationCreateDto.getEvents();
        if (eventIds == null) {
            eventIds = Collections.emptyList();
        } else {
            eventIds = eventIds.stream()
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        }

        List<Event> events = eventRepository.findAllById(eventIds);

        return CompilationMapper.toRequestDto(
                compilationRepository.save(
                        CompilationMapper.toEntity(compilationCreateDto, events)
                )
        );
    }

    @Override
    public CompilationRequestDto update(CompilationUpdateDto compilationUpdateDto, Long compId) {
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new CompilationNotFoundException(compId));

        if (compilationUpdateDto.getTitle() != null) {
            compilation.setTitle(compilationUpdateDto.getTitle());
        }

        if (compilationUpdateDto.getPinned() != null) {
            compilation.setPinned(compilationUpdateDto.getPinned());
        }

        if (compilationUpdateDto.getEvents() != null) {
            List<Event> events = eventRepository.findAllById(compilationUpdateDto.getEvents());

            compilation.setEvents(events);
        }

        return CompilationMapper.toRequestDto(
                compilationRepository.save(compilation)
        );
    }

    @Override
    public void delete(Long compId) {
        compilationRepository.findById(compId)
                .orElseThrow(() -> new CompilationNotFoundException(compId));

        compilationRepository.deleteById(compId);
    }

    @Override
    public List<CompilationRequestDto> get(Boolean pinned, int from, int size) {
        Pageable pageable = PageRequest.of(from / size, size);

        Page<Compilation> page;

        if (pinned == null) {
            page = compilationRepository.findAll(pageable);
        } else {
            page = compilationRepository.findAllByPinned(pinned, pageable);
        }

        return page.stream()
                .map(CompilationMapper::toRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    public CompilationRequestDto getById(Long compId) {
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new CompilationNotFoundException(compId));

        return CompilationMapper.toRequestDto(compilation);
    }
}
