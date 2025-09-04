package ru.yandex.practicum.ewm.compilation;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.ewm.compilation.dto.CompilationCreateDto;
import ru.yandex.practicum.ewm.compilation.dto.CompilationRequestDto;
import ru.yandex.practicum.ewm.compilation.dto.CompilationUpdateDto;
import ru.yandex.practicum.ewm.compilation.service.CompilationService;

@RestController
@AllArgsConstructor
@RequestMapping(path = "/admin/compilations")
public class CompilationAdminController {

    private final CompilationService compilationService;

    @PostMapping
    public ResponseEntity<CompilationRequestDto> create(@RequestBody @Valid CompilationCreateDto compilationCreateDto) {
        CompilationRequestDto compilationRequestDto = compilationService.create(compilationCreateDto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .contentType(MediaType.APPLICATION_JSON)
                .body(compilationRequestDto);
    }

    @PatchMapping("/{compId}")
    public ResponseEntity<CompilationRequestDto> update(@RequestBody @Valid CompilationUpdateDto compilationUpdateDto,
                                                        @PathVariable Long compId) {
        CompilationRequestDto compilationRequestDto = compilationService.update(compilationUpdateDto, compId);

        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(compilationRequestDto);
    }

    @DeleteMapping("/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long compId) {
        compilationService.delete(compId);
    }
}
