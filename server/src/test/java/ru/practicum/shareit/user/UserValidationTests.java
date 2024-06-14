package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.*;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class UserValidationTests {

    @Autowired
    private UserService userService;

    @MockBean
    private UserRepository userRepository;

    private final ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = validatorFactory.getValidator();

    @Test
    void testValidUser() {
        User user = new User(null, "John Doe", "john.doe@example.com");

        assertDoesNotThrow(() -> userService.createUser(user));
    }


    @Test
    void testUserWithoutName() {
        User user = new User(null, null, "john.doe@example.com");

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(violation -> violation.getMessage().contains("Не указано имя пользователя (name)!")));
    }

    @Test
    void testUserWithoutEmail() {
        User user = new User(null, "John Doe", null);

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(violation -> violation.getMessage().contains("Не указан адрес электронной почты (email)!")));
    }

}