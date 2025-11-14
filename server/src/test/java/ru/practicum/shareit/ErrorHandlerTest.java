package ru.practicum.shareit;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.practicum.shareit.exception.AccessViolationException;
import ru.practicum.shareit.exception.ErrorHandler;
import ru.practicum.shareit.exception.ErrorResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ErrorHandlerTest {

    private ErrorHandler errorHandler;
    private HttpServletRequest request;

    @BeforeEach
    void setUp() {
        errorHandler = new ErrorHandler();
        request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("/test-uri");
    }

    @Test
    void handle_genericException_returnsInternalServerError() {
        Exception ex = new Exception("Something went wrong");

        ResponseEntity<ErrorResponse> response = errorHandler.handle(ex, request);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("INTERNAL_SERVER_ERROR", response.getBody().getError());
        assertEquals("Something went wrong", response.getBody().getMessage());
        assertEquals("/test-uri", response.getBody().getPath());
        assertNotNull(response.getBody().getTimestamp());
    }

    @Test
    void handleInternalError_returnsInternalServerError() {
        Exception ex = new Exception("Internal failure");

        // Метод теперь принимает InternalError
        ResponseEntity<ErrorResponse> response = errorHandler.handleInternalError(ex, request);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("INTERNAL_SERVER_ERROR", response.getBody().getError());
        assertEquals("Internal failure", response.getBody().getMessage());
        assertEquals("/test-uri", response.getBody().getPath());
    }

    @Test
    void handleIllegalArgumentException_accessViolation_returnsForbidden() {
        AccessViolationException ex = new AccessViolationException("Access denied");

        ResponseEntity<ErrorResponse> response = errorHandler.handleIllegalArgumentException(ex, request);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("FORBIDDEN", response.getBody().getError());
        assertEquals("Access denied", response.getBody().getMessage());
        assertEquals("/test-uri", response.getBody().getPath());
    }

    @Test
    void handleConstraintViolationException_returnsInternalServerError() {
        ConstraintViolationException ex = new ConstraintViolationException("Validation failed", null);

        ResponseEntity<ErrorResponse> response = errorHandler.handleConstraintViolationException(ex, request);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("INTERNAL_SERVER_ERROR", response.getBody().getError());
        assertEquals("Validation failed", response.getBody().getMessage());
        assertEquals("/test-uri", response.getBody().getPath());
    }
}
