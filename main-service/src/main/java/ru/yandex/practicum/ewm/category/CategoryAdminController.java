package ru.yandex.practicum.ewm.category;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.ewm.category.dto.CategoryCreateDto;
import ru.yandex.practicum.ewm.category.dto.CategoryRequestDto;
import ru.yandex.practicum.ewm.category.service.CategoryService;

@RestController
@AllArgsConstructor
@RequestMapping(path = "/admin/categories")
public class CategoryAdminController {

    private final CategoryService categoryService;

    @PostMapping
    public ResponseEntity<CategoryRequestDto> create(@RequestBody @Valid CategoryCreateDto categoryCreateDto) {
        CategoryRequestDto categoryRequestDto = categoryService.create(categoryCreateDto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .contentType(MediaType.APPLICATION_JSON)
                .body(categoryRequestDto);
    }

    @PatchMapping("/{catId}")
    public ResponseEntity<CategoryRequestDto> update(@RequestBody @Valid CategoryCreateDto categoryCreateDto,
                                                     @PathVariable Long catId) {
        CategoryRequestDto categoryRequestDto = categoryService.update(categoryCreateDto, catId);

        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(categoryRequestDto);
    }

    @DeleteMapping("/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long catId) {
        categoryService.delete(catId);
    }
}
