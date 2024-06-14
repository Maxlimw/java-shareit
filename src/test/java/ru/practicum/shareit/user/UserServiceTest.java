package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.EmailAlreadyExistsException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.ValidationException;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserServiceTest {

    private final UserService userService;

    private User user = new User(1L, "user1", "first@user.ru");

    @Test
    void shouldUpdateUser() {
        UserDto returnUserDto = userService.createUser(user);
        user.setId(returnUserDto.getId());

        user.setName("newName");
        user.setEmail("new@email.ru");

        userService.updateUser(user, returnUserDto.getId());
        UserDto updateUserDto = userService.getUser(returnUserDto.getId());

        assertThat(updateUserDto.getName(), equalTo("newName"));
        assertThat(updateUserDto.getEmail(), equalTo("new@email.ru"));
    }

    @Test
    void shouldExceptionWhenUpdateUserWithExistingEmail() {
        user = new User(2L, "user2", "second@user.ru");
        userService.createUser(user);

        User newUser = new User(3L, "user3", "third@user.ru");
        UserDto returnUserDto = userService.createUser(newUser);

        newUser.setId(returnUserDto.getId());
        newUser.setEmail("second@user.ru");

        final EmailAlreadyExistsException exception = assertThrows(EmailAlreadyExistsException.class,
                () -> userService.updateUser(newUser, newUser.getId()));
        assertEquals(String.format("E-mail '%s' занят другим пользователем!", newUser.getEmail()),
                exception.getMessage());
    }

    @Test
    void shouldExceptionWhenUpdateUserWithEmailInWrongFormat() {
        UserDto returnUserDto = userService.createUser(user);
        user.setEmail("wrong_format");

        final ValidationException exception = assertThrows(ValidationException.class,
                () -> userService.updateUser(user, returnUserDto.getId()));
        assertEquals(exception.getMessage(), "Неверный формат поля email!");
    }

    @Test
    void shouldReturnUserWhenGetUserById() {
        UserDto returnUserDto = userService.createUser(user);

        assertThat(returnUserDto.getName(), equalTo(user.getName()));
        assertThat(returnUserDto.getEmail(), equalTo(user.getEmail()));
    }

    @Test
    void shouldDeleteUser() {
        user = new User(10L, "user10", "ten@user.ru");
        UserDto returnUserDto = userService.createUser(user);

        userService.deleteUser(returnUserDto.getId());
        List<UserDto> listUser = userService.getAllUsers();

        assertThat(listUser.size(), equalTo(0));
    }
}