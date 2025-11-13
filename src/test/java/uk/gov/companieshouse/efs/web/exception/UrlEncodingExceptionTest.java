package uk.gov.companieshouse.efs.web.exception;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UrlEncodingExceptionTest {

    @Test
    void constructorSetsMessageAndCause() {
        Throwable cause = new Throwable("Root cause");
        UrlEncodingException exception = new UrlEncodingException("Error occurred", cause);

        assertEquals("Error occurred", exception.getMessage());
        assertEquals(cause, exception.getCause());
    }

    @Test
    void constructorHandlesNullMessageAndCause() {
        UrlEncodingException exception = new UrlEncodingException(null, null);

        assertNull(exception.getMessage());
        assertNull(exception.getCause());
    }
}