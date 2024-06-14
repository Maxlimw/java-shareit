package ru.practicum.shareit.exceptions;

public class EmailAlreadyExistsException extends RuntimeException  {
    public EmailAlreadyExistsException(String errorMessage) {
        super(errorMessage);
    }
}
