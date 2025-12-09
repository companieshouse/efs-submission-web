package uk.gov.companieshouse.efs.web.controller;

import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import uk.gov.companieshouse.logging.Logger;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
@Component
public class GlobalExceptionHandler {
    private final Logger logger;
    private static final Pattern SUBMISSION_ID_PATTERN = Pattern.compile("[0-9a-fA-F]{24}");
    private static final String SERVICE_PROBLEM_PAGE = ViewConstants.ERROR.asView();

    @Autowired
    public GlobalExceptionHandler(final Logger logger) {
        this.logger = logger;
    }

    @ExceptionHandler(NoResourceFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleNoResourceFoundException(final HttpServletRequest request, final NoResourceFoundException ex) {
        final String submissionId = extractSubmissionId(request.getRequestURI());
        logNoResourceFound(ex, submissionId);
        return ViewConstants.MISSING.asView();
    }

    @ExceptionHandler(ResponseStatusException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleResponseStatusException(final HttpServletRequest request, final ResponseStatusException ex) {
        final String submissionId = extractSubmissionId(request.getRequestURI());
        logResponseStatus(ex, submissionId);
        return SERVICE_PROBLEM_PAGE;
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleException(final Exception ex) {
        logger.error("An exception occurred while processing request", ex);
        return SERVICE_PROBLEM_PAGE;
    }

    private static String extractSubmissionId(final String uri) {
        final Matcher matcher = SUBMISSION_ID_PATTERN.matcher(uri);
        return matcher.find() ? matcher.group() : "";
    }

    private void logNoResourceFound(final NoResourceFoundException ex, final String submissionId) {
        final Map<String, Object> details = new HashMap<>();
        details.put("status", ex.getStatusCode().value());
        details.put("message", ex.getMessage());
        details.put("path", ex.getResourcePath());
        logger.errorContext(submissionId, "No resource found for request", ex, details);
    }

    private void logResponseStatus(final ResponseStatusException ex, final String submissionId) {
        final Map<String, Object> details = new HashMap<>();
        details.put("code", ex.getStatusCode().value());
        details.put("message", ex.getMessage());
        logger.errorContext(submissionId, "Received non 200 series response from API", ex, details);
    }
}
