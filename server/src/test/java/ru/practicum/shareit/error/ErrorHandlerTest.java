package ru.practicum.shareit.error;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import ru.practicum.shareit.exceptions.*;
import java.util.List;

import javax.validation.ValidationException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ErrorHandlerTest {

    @Mock
    private UserNotFoundException userNotFoundException;

    @Mock
    private EmailAlreadyExistsException emailAlreadyExistsException;

    @Mock
    private MethodArgumentNotValidException methodArgumentNotValidException;

    @Mock
    private ValidationException validationException;

    @Mock
    private MissingRequestHeaderException missingRequestHeaderException;

    @Mock
    private NotEnoughRightsException notEnoughRightsException;

    @Mock
    private ItemNotFoundException itemNotFoundException;

    @Mock
    private NotAvailableForBookingException notAvailableForBookingException;

    @Mock
    private BookingNotFoundException bookingNotFoundException;

    @Mock
    private ItemRequestNotFoundException itemRequestNotFoundException;

    @InjectMocks
    private ErrorHandler errorHandler;

    @Test
    void handleUserNotFoundException() {
        when(userNotFoundException.getMessage()).thenReturn("User not found");
        Map<String, String> result = errorHandler.handleUserNotFoundException(userNotFoundException);
        assertEquals("User not found", result.get("error"));
    }

    @Test
    void handleEmailAlreadyExistsException() {
        when(emailAlreadyExistsException.getMessage()).thenReturn("Email already exists");
        ErrorResponse result = errorHandler.handleEmailAlreadyExistsException(emailAlreadyExistsException);
        assertEquals("Email already exists", result.getError());
    }

    @Test
    void handleMethodArgumentNotValidException() {
        FieldError fieldError = new FieldError("fieldName", "fieldName", "error message");

        when(methodArgumentNotValidException.getFieldErrors()).thenReturn(List.of(fieldError));

        ErrorResponse result = errorHandler.handleMethodArgumentNotValidException(methodArgumentNotValidException);

        assertEquals("error message", result.getError());
    }

    @Test
    void handleValidationException() {
        when(validationException.getMessage()).thenReturn("Validation failed");
        ErrorResponse result = errorHandler.handleValidationException(validationException);
        assertEquals("Validation failed", result.getError());
    }

    @Test
    void handleNotEnoughRightsException() {
        when(notEnoughRightsException.getMessage()).thenReturn("Insufficient rights");
        ErrorResponse result = errorHandler.handleNotEnoughRightsException(notEnoughRightsException);
        assertEquals("Insufficient rights", result.getError());
    }

    @Test
    void handleItemNotFoundException() {
        when(itemNotFoundException.getMessage()).thenReturn("Item not found");
        ErrorResponse result = errorHandler.handleItemNotFoundException(itemNotFoundException);
        assertEquals("Item not found", result.getError());
    }

    @Test
    void handleNotAvailableForBookingException() {
        when(notAvailableForBookingException.getMessage()).thenReturn("Item not available for booking");
        ErrorResponse result = errorHandler.handleNotAvailableForBookingException(notAvailableForBookingException);
        assertEquals("Item not available for booking", result.getError());
    }

    @Test
    void handleBookingNotFoundException() {
        when(bookingNotFoundException.getMessage()).thenReturn("Booking not found");
        ErrorResponse result = errorHandler.handleBookingNotFoundException(bookingNotFoundException);
        assertEquals("Booking not found", result.getError());
    }

    @Test
    void handleItemRequestNotFoundException() {
        when(itemRequestNotFoundException.getMessage()).thenReturn("Item request not found");
        ErrorResponse result = errorHandler.handleItemRequestNotFoundException(itemRequestNotFoundException);
        assertEquals("Item request not found", result.getError());
    }
}