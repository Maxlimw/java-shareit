package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.EmailAlreadyExistsException;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.ValidationException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.validation.Validator;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private static final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Override
    @Transactional
    public UserDto createUser(User user) throws EmailAlreadyExistsException {
        try {
            return userMapper.toUserDto(userRepository.save(user));
        } catch (Exception e) {
            String errorMessage = String.format("Пользователь с e-mail = '%s' уже зарегистрирован!", user.getEmail());
            log.warn(errorMessage);
            throw new EmailAlreadyExistsException(errorMessage);
        }
    }

    @Override
    @Transactional
    public UserDto updateUser(User user, Long userId) throws UserNotFoundException, EmailAlreadyExistsException {
        userRepository.existsById(userId);
        validateUser(user);

        if (userRepository.existsByEmail(user.getEmail()) && !getUser(userId).getEmail().equals(user.getEmail())) {
            String errorMessage = String.format("E-mail '%s' занят другим пользователем!", user.getEmail());
            log.warn(errorMessage);
            throw new EmailAlreadyExistsException(errorMessage);
        }
        User oldUser = userRepository.getById(userId);

        if (user.getName() != null) {
            oldUser.setName(user.getName());
        }

        if (user.getEmail() != null) {
            oldUser.setEmail(user.getEmail());
        }
        return userMapper.toUserDto(userRepository.save(oldUser));
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto getUser(Long id) throws UserNotFoundException {
        if (!userRepository.existsById(id)) {
            String errorMessage = String.format("Пользовать с id = %d не найден!", id);
            log.warn(errorMessage);
            throw new UserNotFoundException(errorMessage);
        }

        return userMapper.toUserDto(userRepository.getById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(userMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    private void validateUser(User user) {
        if (user.getEmail() != null) {
            Set<ConstraintViolation<User>> violations = validator.validateProperty(user, "email");
            if (violations.size() != 0) {
                throw new ValidationException(violations.iterator().next().getMessage());
            }
        }
    }
}