package uk.gov.companieshouse.efs.web.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import uk.gov.companieshouse.logging.Logger;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {
    private GlobalExceptionHandler handler;

    @Mock
    private Logger logger;

    @Mock
    private HttpServletRequest request;


    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler(logger);
    }

    @Test
    void testHandleNoResourceFoundException() {
        final NoResourceFoundException exception = new NoResourceFoundException(HttpMethod.GET, "/no-such-resource");
        when(request.getRequestURI()).thenReturn("/no-such-resource");

        final String viewName = handler.handleNoResourceFoundException(request, exception);

        assertEquals(ViewConstants.MISSING.asView(), viewName);
        verify(logger).errorContext(eq(""), eq("No resource found for request"), eq(exception),
            argThat(map -> "No static resource /no-such-resource.".equals(map.get("message"))));
    }

    @Test
    void testHandleResponseStatusException() {
        final ResponseStatusException exception = new ResponseStatusException(HttpStatus.UNAUTHORIZED,
            "Response status error");
        when(request.getRequestURI()).thenReturn("/submission/123456789012345678901234");

        final String view = handler.handleResponseStatusException(request, exception);

        assertEquals(ViewConstants.ERROR.asView(), view);
        verify(logger).errorContext(eq("123456789012345678901234"), eq("Received non 200 series response from API"),
            eq(exception), argThat(map -> "401 UNAUTHORIZED \"Response status error\"".equals(map.get("message"))));
    }

    @Test
    void testHandleGenericException() {
        final Exception exception = new Exception("Generic error");

        final String view = handler.handleException(exception);

        assertEquals(ViewConstants.ERROR.asView(), view);
        verify(logger).error("An exception occurred while processing request", exception);
    }
}