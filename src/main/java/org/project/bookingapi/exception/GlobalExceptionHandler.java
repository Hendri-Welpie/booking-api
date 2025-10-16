package org.project.bookingapi.exception;

import jakarta.validation.ConstraintViolationException;
import org.project.bookingapi.model.ApiError;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.Instant;

import static org.springframework.http.HttpStatus.CONFLICT;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final String TRACE_ID = "traceId";

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ProblemDetail> handleConstraintViolationException(final ConstraintViolationException ex, @NonNull final WebRequest request) {
        final var problemDetail = ProblemDetail.forStatusAndDetail(CONFLICT, "Error while processing the request. Please try again.");

        return new ResponseEntity<>(problemDetail, CONFLICT);
    }


    @ExceptionHandler(BookingConflictException.class)
    public ResponseEntity<ApiError> handleConflict(final BookingConflictException bookingConflictException) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ApiError.builder()
                        .timestamp(Instant.now())
                        .status(HttpStatus.CONFLICT.value())
                        .error(HttpStatus.CONFLICT.getReasonPhrase())
                        .message(bookingConflictException.getMessage())
                        .traceId(MDC.get(TRACE_ID))
                        .build());
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(final ResourceNotFoundException resourceNotFoundException) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiError.builder()
                        .timestamp(Instant.now())
                        .status(HttpStatus.NOT_FOUND.value())
                        .error(HttpStatus.NOT_FOUND.getReasonPhrase())
                        .message(resourceNotFoundException.getMessage())
                        .traceId(MDC.get(TRACE_ID))
                        .build());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleAll(final Exception exception) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiError.builder()
                        .timestamp(Instant.now())
                        .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                        .error(HttpStatus.NOT_FOUND.getReasonPhrase())
                        .message(exception.getMessage())
                        .traceId(MDC.get(TRACE_ID))
                        .build());
    }
}