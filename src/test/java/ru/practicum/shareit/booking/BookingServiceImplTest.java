package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.exceptions.BookingNotFoundException;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class BookingServiceImplTest {

    @Mock
    private BookingRepository mockBookingRepository;

    @Test
    void shouldExceptionWhenUpdateStatusOfNotExistingBooking() {
        BookingService bookingService = new BookingServiceImpl(mockBookingRepository, null, null, null);
        Long bookingId = 1L;

        final BookingNotFoundException exception = assertThrows(BookingNotFoundException.class,
                () -> bookingService.updateStatus(bookingId, true, 2L));
        assertEquals(String.format("Бронирование с id = %d не найдено!", bookingId), exception.getMessage());
    }
}
