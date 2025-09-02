package ru.yandex.practicum.ewm.user;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.ewm.user.dto.UserCreateDto;
import ru.yandex.practicum.ewm.user.dto.UserRequestDto;
import ru.yandex.practicum.ewm.user.service.UserService;

import java.util.List;


@RestController
@AllArgsConstructor
@RequestMapping(path = "/admin/users")
public class UserAdminController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserRequestDto> create(@RequestBody @Valid UserCreateDto userCreateDto) {
        UserRequestDto userRequestDto = userService.create(userCreateDto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .contentType(MediaType.APPLICATION_JSON)
                .body(userRequestDto);
    }

    @GetMapping
    public ResponseEntity<List<UserRequestDto>> get(
            @RequestParam(required = false) List<Integer> ids,
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "10") int size
    ) {
        List<UserRequestDto> users = userService.get(ids, from, size);

        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(users);
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long userId) {
        userService.delete(userId);
    }
}
