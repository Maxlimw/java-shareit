package ru.practicum.shareit.exceptions;

public class NotEnoughRightsException extends RuntimeException {
    public NotEnoughRightsException(String msg) {
        super(msg);
    }
}