package ru.practicum.shareit.exceptions;

public class NotAvailableForBookingException extends RuntimeException {
    public NotAvailableForBookingException(String msg) {
        super(msg);
    }
}