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
import ru.practicum.shareit.user.repository.UserRepository;

import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;


@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class BookingServiceImplTest {

    @Mock
    private UserRepository mockUserRepository;

    @Mock
    private BookingRepository mockBookingRepository;

    @Test
    void shouldExceptionWhenUpdateStatusOfNotExistingBooking() {
        BookingService bookingService = new BookingServiceImpl(mockBookingRepository, null, null, null);

        when(mockUserRepository.existsById(any(Long.class)))
                .thenReturn(true);

        when(mockBookingRepository.existsById(any(Long.class)))
                .thenReturn(false);

        Long bookingId = 1L;

        final BookingNotFoundException exception = assertThrows(BookingNotFoundException.class,
                () -> bookingService.updateStatus(bookingId, true, 2L));
        assertEquals(String.format("Бронирование с id = %d не найдено!", bookingId), exception.getMessage());
    }
}
