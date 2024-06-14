package ru.practicum.shareit.exceptions;

public class UserNotFoundException extends RuntimeException  {
    public UserNotFoundException(String errorMessage) {
        super(errorMessage);
    }
}
