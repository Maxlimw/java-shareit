package ru.practicum.shareit.exceptions;

public class ItemNotFoundException extends RuntimeException  {
    public ItemNotFoundException(String errorMessage) {
        super(errorMessage);
    }
}
