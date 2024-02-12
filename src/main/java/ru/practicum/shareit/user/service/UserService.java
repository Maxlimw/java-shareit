package ru.practicum.shareit.user.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import java.util.List;

@Service
public interface UserService {

    UserDto createUser(User user);

    UserDto updateUser(User user, Long userId);

    UserDto getUser(Long id);

    List<UserDto> getAllUsers();

    void deleteUser(Long id);

}