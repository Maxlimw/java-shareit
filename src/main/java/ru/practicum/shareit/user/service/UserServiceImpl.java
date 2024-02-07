package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.EmailAlreadyExistsException;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserDto createUser(User user) throws EmailAlreadyExistsException {
        if (existsByEmail(user.getEmail())) {
            String errorMessage = String.format("Пользователь с e-mail = '%s' уже зарегистрирован!", user.getEmail());
            log.warn(errorMessage);
            throw new EmailAlreadyExistsException(errorMessage);
        }

        return userMapper.toUserDto(userRepository.save(user));
    }

    @Override
    public UserDto updateUser(User user, Long userId) throws UserNotFoundException, EmailAlreadyExistsException {
        if (!existsById(userId)) {
            String errorMessage = String.format("Пользовать с id = %d не найден!", userId);
            log.warn(errorMessage);
            throw new UserNotFoundException(errorMessage);
        }

        if (existsByEmail(user.getEmail()) && !getUser(userId).getEmail().equals(user.getEmail())) {
            String errorMessage = String.format("E-mail '%s' занят другим пользователем!", user.getEmail());
            log.warn(errorMessage);
            throw new EmailAlreadyExistsException(errorMessage);
        }

        return userMapper.toUserDto(userRepository.update(user, userId));
    }

    @Override
    public UserDto getUser(Long id) throws UserNotFoundException {
        if (!existsById(id)) {
            String errorMessage = String.format("Пользовать с id = %d не найден!", id);
            log.warn(errorMessage);
            throw new UserNotFoundException(errorMessage);
        }

        return userMapper.toUserDto(userRepository.get(id));
    }

    @Override
    public List<UserDto> getAllUsers() {
        return userRepository.getAll().stream().map(userMapper::toUserDto).collect(Collectors.toList());
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.delete(id);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.getEmailsList().contains(email);
    }

    @Override
    public boolean existsById(Long id) {
        return userRepository.getIdsList().contains(id);
    }

}