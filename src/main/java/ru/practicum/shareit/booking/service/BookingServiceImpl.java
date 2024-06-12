package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.BookingNotFoundException;
import ru.practicum.shareit.exceptions.ItemNotFoundException;
import ru.practicum.shareit.exceptions.NotAvailableForBookingException;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.item.itemModel.Item;
import ru.practicum.shareit.item.itemRepository.ItemRepository;
import ru.practicum.shareit.page.PageRequestUtils;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.shareit.booking.model.Status.APPROVED;
import static ru.practicum.shareit.booking.model.Status.REJECTED;
import static ru.practicum.shareit.booking.model.Status.WAITING;


@Slf4j
@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingMapper bookingMapper;

    @Transactional
    @Override
    public BookingDto add(BookingInputDto bookingInputDto, Long userId) {
        User user = checkUserExistence(userId);
        Item item = checkItemExistence(bookingInputDto);
        validate(bookingInputDto, userId);

        Booking booking = bookingMapper.toBooking(bookingInputDto);
        booking.setStatus(Status.WAITING);
        booking.setItem(item);
        booking.setBooker(user);

        return bookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Transactional
    @Override
    public BookingDto updateStatus(Long bookingId, Boolean approved, Long userId) {
        Booking booking = checkBookingExistence(bookingId);

        if (!isOwner(userId, booking)) {
            String errorMessage = String.format("У пользователя c id = %d нет вещи c id = %d!", userId,
                    booking.getItem().getId());
            log.warn(errorMessage);
            throw new ItemNotFoundException(errorMessage);
        }

        if (WAITING.equals(booking.getStatus()) && approved) {
            booking.setStatus(APPROVED);
        } else if (WAITING.equals(booking.getStatus())) {
            booking.setStatus(REJECTED);
        } else {
            String errorMessage = String.format("Невозможно изменить статус для бронирования с id = %d!", bookingId);
            log.warn(errorMessage);
            throw new ValidationException(errorMessage);
        }

        return bookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Transactional(readOnly = true)
    @Override
    public BookingDto get(Long bookingId, Long userId) {
        checkUserExistence(userId);
        checkBookingExistence(bookingId);

        Booking booking = bookingRepository.getById(bookingId);

        if (!isBooker(userId, booking) && !isOwner(userId, booking)) {
            String errorMessage = String.format("У пользователя с id = %d нет прав для просмотра информации " +
                    "о бронировании с id = %d!", userId, booking.getId());
            log.warn(errorMessage);
            throw new BookingNotFoundException(errorMessage);
        }

        return bookingMapper.toBookingDto(bookingRepository.getById(bookingId));
    }

    @Transactional(readOnly = true)
    @Override
    public List<BookingDto> getAllBookingsByUserId(String state, Integer from, Integer size, Long userId) {
        checkUserExistence(userId);
        checkStateExistence(state);

        LocalDateTime dateTime = LocalDateTime.now();
        Sort sort = Sort.by(Sort.Direction.DESC, "start");
        PageRequest pageRequest = PageRequestUtils.getPageRequest(from, size, sort);

        switch (State.valueOf(state)) {
            case CURRENT:
                return bookingRepository.findByBookerIdAndStartIsBeforeAndEndIsAfter(userId, dateTime, dateTime,
                                pageRequest).stream()
                        .map(bookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            case PAST:
                return bookingRepository.findByBookerIdAndStatusAndEndIsBefore(userId, APPROVED, dateTime,
                                pageRequest).stream()
                        .map(bookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            case FUTURE:
                return bookingRepository.findByBookerIdAndStatusInAndStartIsAfter(userId, List.of(APPROVED, WAITING),
                                dateTime, pageRequest).stream()
                        .map(bookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            case WAITING:
                return bookingRepository.findByBookerIdAndStatus(userId, Status.WAITING, pageRequest).stream()
                        .map(bookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            case REJECTED:
                return bookingRepository.findByBookerIdAndStatus(userId, Status.REJECTED, pageRequest).stream()
                        .map(bookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            default:
                return bookingRepository.findByBookerId(userId, pageRequest).stream()
                        .map(bookingMapper::toBookingDto)
                        .collect(Collectors.toList());
        }
    }

    @Transactional(readOnly = true)
    @Override
    public List<BookingDto> getAllBookingsForUserItems(String state, Integer from, Integer size, Long userId) {
        checkUserExistence(userId);
        checkStateExistence(state);

        LocalDateTime dateTime = LocalDateTime.now();
        Sort sort = Sort.by(Sort.Direction.DESC, "start");
        PageRequest pageRequest = PageRequestUtils.getPageRequest(from, size, sort);


        switch (State.valueOf(state)) {
            case CURRENT:
                return bookingRepository.findAllBookingsByOwner(userId, pageRequest).stream()
                        .filter(booking -> booking.getStart().isBefore(dateTime) && booking.getEnd().isAfter(dateTime))
                        .map(bookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            case PAST:
                return bookingRepository.findAllBookingsByOwner(userId, List.of(APPROVED), pageRequest).stream()
                        .filter(booking -> booking.getEnd().isBefore(dateTime))
                        .map(bookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            case FUTURE:
                return bookingRepository.findAllBookingsByOwner(userId, List.of(APPROVED, WAITING), pageRequest).stream()
                        .filter(booking -> booking.getStart().isAfter(dateTime))
                        .map(bookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            case WAITING:
                return bookingRepository.findAllBookingsByOwner(userId, List.of(WAITING), pageRequest).stream()
                        .map(bookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            case REJECTED:
                return bookingRepository.findAllBookingsByOwner(userId, List.of(REJECTED), pageRequest).stream()
                        .map(bookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            default:
                return bookingRepository.findAllBookingsByOwner(userId, pageRequest).stream()
                        .map(bookingMapper::toBookingDto)
                        .collect(Collectors.toList());
        }
    }

    private void checkStateExistence(String state) {
        var existingStates = Arrays.stream(State.values())
                .map(Enum::toString)
                .collect(Collectors.toList());

        if (!existingStates.contains(state)) {
            String errorMessage = String.format("Unknown state: %s", state);
            log.warn(errorMessage);
            throw new ValidationException(errorMessage);
        }
    }

    private User checkUserExistence(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> {
                    String errorMessage = String.format("Пользователь c id = %d не найден!", userId);
                    log.warn(errorMessage);
                    return new UserNotFoundException(errorMessage);
                });
    }

    private Item checkItemExistence(BookingInputDto bookingInputDto) {
        Long itemId = bookingInputDto.getItemId();
        return itemRepository.findById(itemId)
                .orElseThrow(() -> {
                    String errorMessage = String.format("Вещь с id = %d не найдена!", itemId);
                    log.warn(errorMessage);
                    return new ItemNotFoundException(errorMessage);
                });
    }

    private Booking checkBookingExistence(Long bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> {
                    String errorMessage = String.format("Бронирование с id = %d не найдено!", bookingId);
                    log.warn(errorMessage);
                    return new BookingNotFoundException(errorMessage);
                });
    }

    private boolean isOwner(Long userId, Booking booking) {
        return itemRepository.getById(booking.getItem().getId()).getOwner().getId().equals(userId);
    }

    private boolean isBooker(Long userId, Booking booking) {
        return booking.getBooker().getId().equals(userId);
    }

    private void validate(BookingInputDto bookingInputDto, Long userId) {
        Item item = itemRepository.getById(bookingInputDto.getItemId());

        if (Boolean.FALSE.equals(item.getAvailable())) {
            String errorMessage = String.format("Вещь с id = %d недоступна для бронирования!", bookingInputDto.getItemId());
            log.warn(errorMessage);
            throw new NotAvailableForBookingException(errorMessage);
        }

        if (itemRepository.getById(bookingInputDto.getItemId()).getOwner().getId().equals(userId)) {
            String errorMessage = "Вы не можете забронировать вещь, для которой являетесь владельцем!";
            log.warn(errorMessage);
            throw new BookingNotFoundException(errorMessage);
        }

        if (bookingInputDto.getEnd().isBefore(bookingInputDto.getStart())
                || bookingInputDto.getEnd().equals(bookingInputDto.getStart())) {
            log.warn("Дата окончания бронирования не может быть раньше или равняться дате начала бронирования!");
            throw new ValidationException("Дата окончания бронирования не может быть раньше или равняться дате " +
                    "начала бронирования!");
        }
    }
}