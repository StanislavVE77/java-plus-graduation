package ru.yandex.practicum.ewm.category;

import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.ewm.category.dto.CategoryRequestDto;
import ru.yandex.practicum.ewm.category.service.CategoryService;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping(path = "/categories")
public class CategoryPublicController {

    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<List<CategoryRequestDto>> get(
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "10") int size
    ) {
        List<CategoryRequestDto> categories = categoryService.get(from, size);

        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(categories);
    }

    @GetMapping("/{catId}")
    public ResponseEntity<CategoryRequestDto> getById(@PathVariable Long catId) {
        CategoryRequestDto categoryRequestDto = categoryService.getById(catId);

        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(categoryRequestDto);
    }
}
