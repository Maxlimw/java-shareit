package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exceptions.BookingNotFoundException;
import ru.practicum.shareit.exceptions.NotAvailableForBookingException;
import ru.practicum.shareit.item.itemDto.ItemDto;
import ru.practicum.shareit.item.itemService.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingServiceTest {

    private final BookingService bookingService;
    private final UserService userService;
    private final ItemService itemService;

    private final User user1 = new User(300L, "user1", "first@user.ru");
    private final User user2 = new User(301L, "user2", "second@user.ru");
    private final User user3 = new User(302L, "user3", "third@user.ru");

    private final ItemDto itemDto1 = new ItemDto(301L, "item1", "description1", true, null, null);

    @Test
    void shouldExceptionWhenCreateBookingByOwnerItem() {
        UserDto ownerDto = userService.createUser(user1);
        ItemDto itemDto = itemService.addItem(itemDto1, ownerDto.getId());

        BookingInputDto bookingInputDto = new BookingInputDto(
                itemDto.getId(),
                LocalDateTime.of(2030, 12, 25, 12, 0, 0),
                LocalDateTime.of(2030, 12, 26, 12, 0, 0));

        BookingNotFoundException exp = assertThrows(BookingNotFoundException.class,
                () -> bookingService.add(bookingInputDto, ownerDto.getId()));
        assertEquals("Вы не можете забронировать вещь, для которой являетесь владельцем!", exp.getMessage());
    }

    @Test
    void shouldSetStatusToApprovedWhenUpdateStatusByOwnerAndApprovedTrue() {
        UserDto ownerDto = userService.createUser(user1);
        UserDto bookerDto = userService.createUser(user2);
        ItemDto itemDto = itemService.addItem(itemDto1, ownerDto.getId());

        BookingInputDto bookingInputDto = new BookingInputDto(
                itemDto.getId(),
                LocalDateTime.of(2030, 12, 25, 12, 0, 0),
                LocalDateTime.of(2030, 12, 26, 12, 0, 0));

        BookingDto bookingDto = bookingService.add(bookingInputDto, bookerDto.getId());
        bookingDto = bookingService.updateStatus(bookingDto.getId(), true, ownerDto.getId());

        assertEquals(Status.APPROVED, bookingDto.getStatus());
    }

    @Test
    void shouldSetStatusToRejectedWhenUpdateStatusByOwnerAndApprovedFalse() {
        UserDto ownerDto = userService.createUser(user1);
        UserDto bookerDto = userService.createUser(user2);
        ItemDto itemDto = itemService.addItem(itemDto1, ownerDto.getId());

        BookingInputDto bookingInputDto = new BookingInputDto(
                itemDto.getId(),
                LocalDateTime.of(2030, 12, 25, 12, 0, 0),
                LocalDateTime.of(2030, 12, 26, 12, 0, 0));

        BookingDto bookingDto = bookingService.add(bookingInputDto, bookerDto.getId());
        bookingDto = bookingService.updateStatus(bookingDto.getId(), false, ownerDto.getId());

        assertEquals(Status.REJECTED, bookingDto.getStatus());
    }

    @Test
    void shouldExceptionWhenUpdateStatusByOwnerAndStatusRejected() {
        UserDto ownerDto = userService.createUser(user1);
        UserDto bookerDto = userService.createUser(user2);
        ItemDto itemDto = itemService.addItem(itemDto1, ownerDto.getId());

        BookingInputDto bookingInputDto = new BookingInputDto(
                itemDto.getId(),
                LocalDateTime.of(2030, 12, 25, 12, 0, 0),
                LocalDateTime.of(2030, 12, 26, 12, 0, 0));

        BookingDto bookingDto = bookingService.add(bookingInputDto, bookerDto.getId());
        bookingDto = bookingService.updateStatus(bookingDto.getId(), false, ownerDto.getId());
        Long bookingId = bookingDto.getId();

        ValidationException exp = assertThrows(ValidationException.class,
                () -> bookingService.updateStatus(bookingId, false, ownerDto.getId()));
        assertEquals(String.format("Невозможно изменить статус для бронирования с id = %d!", bookingId),
                exp.getMessage());
    }

    @Test
    void shouldExceptionWhenGetBookingByNotOwnerOrNotBooker() {
        UserDto ownerDto = userService.createUser(user1);
        UserDto bookerDto = userService.createUser(user2);
        UserDto otherUserDto = userService.createUser(user3);

        Long otherUserId = otherUserDto.getId();
        ItemDto newItemDto = itemService.addItem(itemDto1, ownerDto.getId());

        BookingInputDto bookingInputDto = new BookingInputDto(
                newItemDto.getId(),
                LocalDateTime.of(2030, 12, 25, 12, 0, 0),
                LocalDateTime.of(2030, 12, 26, 12, 0, 0));

        BookingDto bookingDto = bookingService.add(bookingInputDto, bookerDto.getId());

        BookingNotFoundException exp = assertThrows(BookingNotFoundException.class,
                () -> bookingService.get(bookingDto.getId(), otherUserId));
        assertEquals(String.format("У пользователя с id = %d нет прав для просмотра информации о бронировании с id = %d!",
                otherUserId, bookingDto.getId()), exp.getMessage());
    }

    @Test
    void shouldExceptionWhenGetAllBookingsInUnknownStateByUserId() {
        UserDto ownerDto = userService.createUser(user1);
        UserDto bookerDto = userService.createUser(user2);
        ItemDto itemDto = itemService.addItem(itemDto1, ownerDto.getId());

        BookingInputDto bookingInputDto = new BookingInputDto(
                itemDto.getId(),
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(10));
        bookingService.add(bookingInputDto, bookerDto.getId());

        BookingInputDto bookingInputDto1 = new BookingInputDto(
                itemDto.getId(),
                LocalDateTime.now().plusMinutes(5),
                LocalDateTime.now().plusYears(1));
        bookingService.add(bookingInputDto1, bookerDto.getId());

        ValidationException exp = assertThrows(ValidationException.class,
                () -> bookingService.getAllBookingsByUserId("UNKNOWN_STATE", 0, null, bookerDto.getId()));
        assertEquals("Unknown state: UNKNOWN_STATE", exp.getMessage());
    }

    @Test
    void shouldReturnBookingsWhenGetAllBookingsByUserIdAndSizeIsNull() {
        UserDto ownerDto = userService.createUser(user1);
        UserDto bookerDto = userService.createUser(user2);
        ItemDto itemDto = itemService.addItem(itemDto1, ownerDto.getId());

        BookingInputDto bookingInputDto1 = new BookingInputDto(
                itemDto.getId(),
                LocalDateTime.of(2030, 12, 25, 12, 0, 0),
                LocalDateTime.of(2030, 12, 26, 12, 0, 0));
        bookingService.add(bookingInputDto1, bookerDto.getId());

        BookingInputDto bookingInputDto2 = new BookingInputDto(
                itemDto.getId(),
                LocalDateTime.of(2031, 12, 25, 12, 0, 0),
                LocalDateTime.of(2031, 12, 26, 12, 0, 0));
        bookingService.add(bookingInputDto2, bookerDto.getId());

        List<BookingDto> listBookings = bookingService.getAllBookingsByUserId("ALL", 0, null, bookerDto.getId());
        assertEquals(2, listBookings.size());
    }

    @Test
    void shouldReturnBookingsWhenGetAllBookingsInFutureStateByUserIdAndSizeIsNull() {
        UserDto ownerDto = userService.createUser(user1);
        UserDto bookerDto = userService.createUser(user2);
        ItemDto itemDto = itemService.addItem(itemDto1, ownerDto.getId());

        BookingInputDto bookingInputDto = new BookingInputDto(
                itemDto.getId(),
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(10));
        bookingService.add(bookingInputDto, bookerDto.getId());

        BookingInputDto bookingInputDto1 = new BookingInputDto(
                itemDto.getId(),
                LocalDateTime.now().plusMinutes(5),
                LocalDateTime.now().plusYears(1));
        bookingService.add(bookingInputDto1, bookerDto.getId());

        List<BookingDto> listBookings = bookingService.getAllBookingsByUserId("FUTURE", 0, null, bookerDto.getId());
        assertEquals(2, listBookings.size());
    }

    @Test
    void shouldReturnBookingsWhenGetAllBookingsInFutureStateByUserIdAndSizeNotNull() {
        UserDto ownerDto = userService.createUser(user1);
        UserDto bookerDto = userService.createUser(user2);
        ItemDto itemDto = itemService.addItem(itemDto1, ownerDto.getId());

        BookingInputDto bookingInputDto = new BookingInputDto(
                itemDto.getId(),
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(10));
        bookingService.add(bookingInputDto, bookerDto.getId());

        BookingInputDto bookingInputDto1 = new BookingInputDto(
                itemDto.getId(),
                LocalDateTime.now().plusMinutes(5),
                LocalDateTime.now().plusYears(1));
        bookingService.add(bookingInputDto1, bookerDto.getId());

        List<BookingDto> listBookings = bookingService.getAllBookingsByUserId("FUTURE", 0, 1, bookerDto.getId());
        assertEquals(1, listBookings.size());
    }

    @Test
    void shouldReturnBookingsWhenGetAllBookingsInWaitingStateByUserIdAndSizeIsNull() {
        UserDto ownerDto = userService.createUser(user1);
        UserDto bookerDto = userService.createUser(user2);
        ItemDto itemDto = itemService.addItem(itemDto1, ownerDto.getId());

        BookingInputDto bookingInputDto = new BookingInputDto(
                itemDto.getId(),
                LocalDateTime.of(2030, 12, 25, 12, 0, 0),
                LocalDateTime.of(2030, 12, 26, 12, 0, 0));
        bookingService.add(bookingInputDto, bookerDto.getId());

        BookingInputDto bookingInputDto1 = new BookingInputDto(
                itemDto.getId(),
                LocalDateTime.of(2031, 12, 25, 12, 0, 0),
                LocalDateTime.of(2031, 12, 26, 12, 0, 0));
        bookingService.add(bookingInputDto1, bookerDto.getId());

        List<BookingDto> listBookings = bookingService.getAllBookingsByUserId("WAITING", 0, null, bookerDto.getId());
        assertEquals(2, listBookings.size());
    }

    @Test
    void shouldReturnBookingsWhenGetAllBookingsInWaitingStateByUserIdAndSizeNotNull() {
        UserDto ownerDto = userService.createUser(user1);
        UserDto bookerDto = userService.createUser(user2);
        ItemDto itemDto = itemService.addItem(itemDto1, ownerDto.getId());

        BookingInputDto bookingInputDto = new BookingInputDto(
                itemDto.getId(),
                LocalDateTime.of(2030, 12, 25, 12, 0, 0),
                LocalDateTime.of(2030, 12, 26, 12, 0, 0));
        bookingService.add(bookingInputDto, bookerDto.getId());

        BookingInputDto bookingInputDto1 = new BookingInputDto(
                itemDto.getId(),
                LocalDateTime.of(2031, 12, 25, 12, 0, 0),
                LocalDateTime.of(2031, 12, 26, 12, 0, 0));
        bookingService.add(bookingInputDto1, bookerDto.getId());

        List<BookingDto> listBookings = bookingService.getAllBookingsByUserId("WAITING", 0, 1, bookerDto.getId());
        assertEquals(1, listBookings.size());
    }

    @Test
    void shouldReturnNoBookingsWhenGetAllBookingsInRejectedStateByUserIdAndSizeIsNull() {
        UserDto ownerDto = userService.createUser(user1);
        UserDto bookerDto = userService.createUser(user2);
        ItemDto itemDto = itemService.addItem(itemDto1, ownerDto.getId());

        BookingInputDto bookingInputDto = new BookingInputDto(
                itemDto.getId(),
                LocalDateTime.of(2030, 12, 25, 12, 0, 0),
                LocalDateTime.of(2030, 12, 26, 12, 0, 0));
        bookingService.add(bookingInputDto, bookerDto.getId());

        BookingInputDto bookingInputDto1 = new BookingInputDto(
                itemDto.getId(),
                LocalDateTime.of(2031, 12, 25, 12, 0, 0),
                LocalDateTime.of(2031, 12, 26, 12, 0, 0));
        bookingService.add(bookingInputDto1, bookerDto.getId());

        List<BookingDto> listBookings = bookingService.getAllBookingsByUserId("REJECTED", 0, null, bookerDto.getId());
        assertEquals(0, listBookings.size());
    }

    @Test
    void shouldReturnBookingsWhenGetAllBookingsInRejectedStateByUserIdAndSizeNotNull() {
        UserDto ownerDto = userService.createUser(user1);
        UserDto bookerDto = userService.createUser(user2);
        ItemDto itemDto = itemService.addItem(itemDto1, ownerDto.getId());

        BookingInputDto bookingInputDto1 = new BookingInputDto(
                itemDto.getId(),
                LocalDateTime.of(2030, 12, 25, 12, 0, 0),
                LocalDateTime.of(2030, 12, 26, 12, 0, 0));
        bookingService.add(bookingInputDto1, bookerDto.getId());

        BookingInputDto bookingInputDto2 = new BookingInputDto(
                itemDto.getId(),
                LocalDateTime.of(2031, 12, 25, 12, 0, 0),
                LocalDateTime.of(2031, 12, 26, 12, 0, 0));
        BookingDto bookingDto2 = bookingService.add(bookingInputDto2, bookerDto.getId());

        bookingService.updateStatus(bookingDto2.getId(), false, ownerDto.getId());

        List<BookingDto> listBookings = bookingService.getAllBookingsByUserId("REJECTED", 0, 1, bookerDto.getId());
        assertEquals(1, listBookings.size());
    }

    @Test
    void shouldReturnBookingsWhenGetAllBookingsForUserItemsAndSizeIsNull() {
        UserDto ownerDto = userService.createUser(user1);
        UserDto bookerDto = userService.createUser(user2);
        ItemDto itemDto = itemService.addItem(itemDto1, ownerDto.getId());

        BookingInputDto bookingInputDto = new BookingInputDto(
                itemDto.getId(),
                LocalDateTime.of(2030, 12, 25, 12, 0, 0),
                LocalDateTime.of(2030, 12, 26, 12, 0, 0));
        bookingService.add(bookingInputDto, bookerDto.getId());

        BookingInputDto bookingInputDto1 = new BookingInputDto(
                itemDto.getId(),
                LocalDateTime.of(2031, 12, 25, 12, 0, 0),
                LocalDateTime.of(2031, 12, 26, 12, 0, 0));
        bookingService.add(bookingInputDto1, bookerDto.getId());

        List<BookingDto> listBookings = bookingService.getAllBookingsForUserItems("ALL", 0, null, ownerDto.getId());
        assertEquals(2, listBookings.size());
    }

    @Test
    void shouldReturnBookingsWhenGetAllBookingsForUserItemsAndSizeNotNull() {
        UserDto ownerDto = userService.createUser(user1);
        UserDto bookerDto = userService.createUser(user2);
        ItemDto itemDto = itemService.addItem(itemDto1, ownerDto.getId());

        BookingInputDto bookingInputDto = new BookingInputDto(
                itemDto.getId(),
                LocalDateTime.of(2030, 12, 25, 12, 0, 0),
                LocalDateTime.of(2030, 12, 26, 12, 0, 0));
        bookingService.add(bookingInputDto, bookerDto.getId());

        BookingInputDto bookingInputDto1 = new BookingInputDto(
                itemDto.getId(),
                LocalDateTime.of(2031, 12, 25, 12, 0, 0),
                LocalDateTime.of(2031, 12, 26, 12, 0, 0));
        bookingService.add(bookingInputDto1, bookerDto.getId());

        List<BookingDto> listBookings = bookingService.getAllBookingsForUserItems("ALL", 0, 1, ownerDto.getId());
        assertEquals(1, listBookings.size());
    }

    @Test
    void shouldReturnBookingsWhenGetAllBookingsForUserItemsAndStateFutureAndSizeIsNull() {
        UserDto ownerDto = userService.createUser(user1);
        UserDto bookerDto = userService.createUser(user2);
        ItemDto itemDto = itemService.addItem(itemDto1, ownerDto.getId());

        BookingInputDto bookingInputDto = new BookingInputDto(
                itemDto.getId(),
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(10));
        bookingService.add(bookingInputDto, bookerDto.getId());

        BookingInputDto bookingInputDto1 = new BookingInputDto(
                itemDto.getId(),
                LocalDateTime.now().plusMonths(2),
                LocalDateTime.now().plusMonths(10));
        bookingService.add(bookingInputDto1, bookerDto.getId());

        List<BookingDto> listBookings = bookingService.getAllBookingsForUserItems("FUTURE", 0, null, ownerDto.getId());
        assertEquals(2, listBookings.size());
    }

    @Test
    void shouldReturnBookingsWhenGetAllBookingsForUserItemsAndStateWaitingAndSizeIsNull() {
        UserDto ownerDto = userService.createUser(user1);
        UserDto bookerDto = userService.createUser(user2);
        ItemDto itemDto = itemService.addItem(itemDto1, ownerDto.getId());

        BookingInputDto bookingInputDto = new BookingInputDto(
                itemDto.getId(),
                LocalDateTime.of(2030, 12, 25, 12, 0, 0),
                LocalDateTime.of(2030, 12, 26, 12, 0, 0));
        bookingService.add(bookingInputDto, bookerDto.getId());

        BookingInputDto bookingInputDto1 = new BookingInputDto(
                itemDto.getId(),
                LocalDateTime.of(2031, 12, 25, 12, 0, 0),
                LocalDateTime.of(2031, 12, 26, 12, 0, 0));
        bookingService.add(bookingInputDto1, bookerDto.getId());

        List<BookingDto> listBookings = bookingService.getAllBookingsForUserItems("WAITING", 0, null, ownerDto.getId());
        assertEquals(2, listBookings.size());
    }

    @Test
    void shouldReturnBookingsWhenGetAllBookingsForUserItemsAndStateWaitingAndSizeNotNull() {
        UserDto ownerDto = userService.createUser(user1);
        UserDto bookerDto = userService.createUser(user2);
        ItemDto itemDto = itemService.addItem(itemDto1, ownerDto.getId());

        BookingInputDto bookingInputDto = new BookingInputDto(
                itemDto.getId(),
                LocalDateTime.of(2030, 12, 25, 12, 0, 0),
                LocalDateTime.of(2030, 12, 26, 12, 0, 0));
        bookingService.add(bookingInputDto, bookerDto.getId());

        BookingInputDto bookingInputDto1 = new BookingInputDto(
                itemDto.getId(),
                LocalDateTime.of(2031, 12, 25, 12, 0, 0),
                LocalDateTime.of(2031, 12, 26, 12, 0, 0));
        bookingService.add(bookingInputDto1, bookerDto.getId());

        List<BookingDto> listBookings = bookingService.getAllBookingsForUserItems("WAITING", 0, 1, ownerDto.getId());
        assertEquals(1, listBookings.size());
    }

    @Test
    void shouldReturnBookingsWhenGetAllBookingsForUserItemsAndStateRejectedAndSizeIsNull() {
        UserDto ownerDto = userService.createUser(user1);
        UserDto bookerDto = userService.createUser(user2);
        ItemDto itemDto = itemService.addItem(itemDto1, ownerDto.getId());

        BookingInputDto bookingInputDto = new BookingInputDto(
                itemDto.getId(),
                LocalDateTime.of(2030, 12, 25, 12, 0, 0),
                LocalDateTime.of(2030, 12, 26, 12, 0, 0));
        bookingService.add(bookingInputDto, bookerDto.getId());

        BookingInputDto bookingInputDto1 = new BookingInputDto(
                itemDto.getId(),
                LocalDateTime.of(2031, 12, 25, 12, 0, 0),
                LocalDateTime.of(2031, 12, 26, 12, 0, 0));
        bookingService.add(bookingInputDto1, bookerDto.getId());

        List<BookingDto> listBookings = bookingService.getAllBookingsForUserItems("REJECTED", 0, null, ownerDto.getId());
        assertEquals(0, listBookings.size());
    }

    @Test
    void shouldReturnBookingsWhenGetAllBookingsForUserItemsAndStateRejectedAndSizeNotNull() {
        UserDto ownerDto = userService.createUser(user1);
        UserDto bookerDto = userService.createUser(user2);
        ItemDto itemDto = itemService.addItem(itemDto1, ownerDto.getId());

        BookingInputDto bookingInputDto1 = new BookingInputDto(
                itemDto.getId(),
                LocalDateTime.of(2030, 12, 25, 12, 0, 0),
                LocalDateTime.of(2030, 12, 26, 12, 0, 0));
        bookingService.add(bookingInputDto1, bookerDto.getId());

        BookingInputDto bookingInputDto2 = new BookingInputDto(
                itemDto.getId(),
                LocalDateTime.of(2031, 12, 25, 12, 0, 0),
                LocalDateTime.of(2031, 12, 26, 12, 0, 0));
        bookingService.add(bookingInputDto2, bookerDto.getId());

        List<BookingDto> listBookings = bookingService.getAllBookingsForUserItems("REJECTED", 0, 1, ownerDto.getId());
        assertEquals(0, listBookings.size());
    }

    @Test
    void shouldExceptionWhenItemIsNotAvailableForBooking() {
        UserDto ownerDto = userService.createUser(user1);
        UserDto bookerDto = userService.createUser(user2);

        itemDto1.setAvailable(Boolean.FALSE);
        ItemDto itemDto = itemService.addItem(itemDto1, ownerDto.getId());

        BookingInputDto bookingInputDto = new BookingInputDto(
                itemDto.getId(),
                LocalDateTime.of(2030, 12, 25, 12, 0, 0),
                LocalDateTime.of(2030, 12, 26, 12, 0, 0));

        NotAvailableForBookingException exp = assertThrows(NotAvailableForBookingException.class,
                () -> bookingService.add(bookingInputDto, bookerDto.getId()));
        assertEquals(String.format("Вещь с id = %d недоступна для бронирования!", itemDto.getId()), exp.getMessage());
    }

    @Test
    void shouldExceptionWhenBookingEndIsBeforeStart() {
        UserDto ownerDto = userService.createUser(user1);
        UserDto bookerDto = userService.createUser(user2);
        ItemDto itemDto = itemService.addItem(itemDto1, ownerDto.getId());

        BookingInputDto bookingInputDto = new BookingInputDto(
                itemDto.getId(),
                LocalDateTime.now().minusDays(5),
                LocalDateTime.now().minusDays(10));

        ValidationException exp = assertThrows(ValidationException.class,
                () -> bookingService.add(bookingInputDto, bookerDto.getId()));
        assertEquals("Дата окончания бронирования не может быть раньше или равняться дате начала бронирования!",
                exp.getMessage());
    }

    @Test
    void shouldExceptionWhenBookingEndEqualsStart() {
        UserDto ownerDto = userService.createUser(user1);
        UserDto bookerDto = userService.createUser(user2);
        ItemDto itemDto = itemService.addItem(itemDto1, ownerDto.getId());

        LocalDateTime currentDateTime = LocalDateTime.now();
        BookingInputDto bookingInputDto = new BookingInputDto(
                itemDto.getId(),
                currentDateTime,
                currentDateTime);

        ValidationException exp = assertThrows(ValidationException.class,
                () -> bookingService.add(bookingInputDto, bookerDto.getId()));
        assertEquals("Дата окончания бронирования не может быть раньше или равняться дате начала бронирования!",
                exp.getMessage());
    }

    @Test
    void shouldWhenGetAllBookingsForUserItemsAndFromIsNegative() {
        UserDto ownerDto = userService.createUser(user1);
        UserDto bookerDto = userService.createUser(user2);
        ItemDto itemDto = itemService.addItem(itemDto1, ownerDto.getId());

        BookingInputDto bookingInputDto = new BookingInputDto(
                itemDto.getId(),
                LocalDateTime.of(2030, 12, 25, 12, 0, 0),
                LocalDateTime.of(2030, 12, 26, 12, 0, 0));

        bookingService.add(bookingInputDto, bookerDto.getId());

        ValidationException exp = assertThrows(ValidationException.class,
                () -> bookingService.getAllBookingsForUserItems("ALL", -1, 2, ownerDto.getId()));
        assertEquals("Параметр from должен быть >= 0 или равен null!", exp.getMessage());
    }

    @Test
    void shouldWhenGetAllBookingsForUserItemsAndSizeIsNegative() {
        UserDto ownerDto = userService.createUser(user1);
        UserDto bookerDto = userService.createUser(user2);
        ItemDto itemDto = itemService.addItem(itemDto1, ownerDto.getId());

        BookingInputDto bookingInputDto = new BookingInputDto(
                itemDto.getId(),
                LocalDateTime.of(2030, 12, 25, 12, 0, 0),
                LocalDateTime.of(2030, 12, 26, 12, 0, 0));

        bookingService.add(bookingInputDto, bookerDto.getId());

        ValidationException exp = assertThrows(ValidationException.class,
                () -> bookingService.getAllBookingsForUserItems("ALL", 0, -2, ownerDto.getId()));
        assertEquals("Параметр size должен быть больше 0 или равен null!", exp.getMessage());
    }

    @Test
    void shouldWhenGetAllBookingsForUserItemsAndSizeIsZero() {
        UserDto ownerDto = userService.createUser(user1);
        UserDto bookerDto = userService.createUser(user2);
        ItemDto itemDto = itemService.addItem(itemDto1, ownerDto.getId());

        BookingInputDto bookingInputDto = new BookingInputDto(
                itemDto.getId(),
                LocalDateTime.of(2030, 12, 25, 12, 0, 0),
                LocalDateTime.of(2030, 12, 26, 12, 0, 0));

        bookingService.add(bookingInputDto, bookerDto.getId());

        ValidationException exp = assertThrows(ValidationException.class,
                () -> bookingService.getAllBookingsForUserItems("ALL", 0, 0, ownerDto.getId()));
        assertEquals("Параметр size должен быть больше 0 или равен null!", exp.getMessage());
    }
}