package ru.practicum.shareit.exceptions;

public class ItemRequestNotFoundException extends RuntimeException {

    public ItemRequestNotFoundException(String msg) {
        super(msg);
    }
}