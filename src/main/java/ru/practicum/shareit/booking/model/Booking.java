package ru.practicum.shareit.booking.model;

import org.apache.catalina.User;
import ru.practicum.shareit.item.itemModel.Item;
import ru.practicum.shareit.booking.model.Status;
import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */
public class Booking {
    private Long id;

    private LocalDateTime start;

    private LocalDateTime end;

    private Item item;

    private User booker;

    private Status status;
}
