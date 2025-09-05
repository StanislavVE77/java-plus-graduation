package ru.yandex.practicum.ewm.user.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.ewm.exception.UserNotFoundException;
import ru.yandex.practicum.ewm.user.dto.UserCreateDto;
import ru.yandex.practicum.ewm.user.dto.UserRequestDto;
import ru.yandex.practicum.ewm.user.mapper.UserMapper;
import ru.yandex.practicum.ewm.user.model.User;
import ru.yandex.practicum.ewm.user.storage.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserRequestDto create(UserCreateDto userCreateDto) {
        return UserMapper.toRequestDto(
                userRepository.save(
                        UserMapper.toEntity(userCreateDto)
                )
        );
    }

    @Override
    public List<UserRequestDto> get(List<Integer> ids, int from, int size) {
        Pageable pageable = PageRequest.of(from / size, size);

        Page<User> page;

        if (ids == null || ids.isEmpty()) {
            page = userRepository.findAll(pageable);
        } else {
            page = userRepository.findAllByIdIn(ids, pageable);
        }

        return page.stream()
                .map(UserMapper::toRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        userRepository.deleteById(userId);
    }
}
