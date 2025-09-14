package ru.yandex.practicum.ewm.compilation.mapper;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.ewm.compilation.dto.CompilationCreateDto;
import ru.yandex.practicum.ewm.compilation.dto.CompilationRequestDto;
import ru.yandex.practicum.ewm.compilation.model.Compilation;
import ru.yandex.practicum.ewm.event.model.Event;

import java.util.List;

@Component
public class CompilationMapper {
    public static Compilation toEntity(CompilationCreateDto compilationCreateDto, List<Event> events) {
        Compilation compilation = new Compilation();
        compilation.setTitle(compilationCreateDto.getTitle());
        compilation.setPinned(compilationCreateDto.getPinned());
        compilation.setEvents(events);

        return compilation;
    }

    public static CompilationRequestDto toRequestDto(Compilation compilation) {
        return new CompilationRequestDto(
                compilation.getId(),
                compilation.getTitle(),
                compilation.getPinned(),
                compilation.getEvents()
        );
    }
}
