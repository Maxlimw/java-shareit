package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import java.util.List;

public interface UserService {

    UserDto createUser(User user);

    UserDto updateUser(User user, Long userId);

    UserDto getUser(Long id);

    List<UserDto> getAllUsers();

    void deleteUser(Long id);

    boolean existsByEmail(String email);

    void existsById(Long id);
}