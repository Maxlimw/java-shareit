package ru.practicum.shareit.booking.dto;
import ch.qos.logback.core.status.Status;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.catalina.User;
import ru.practicum.shareit.item.itemModel.Item;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class BookingDto {
    private Long id;
    private LocalDate start;
    private LocalDate end;
    private Item item;
    private User booker;
    private Status status;

}
