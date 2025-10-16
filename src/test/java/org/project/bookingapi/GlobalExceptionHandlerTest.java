package org.project.bookingapi;

import org.junit.jupiter.api.Test;
import org.project.bookingapi.exception.BookingConflictException;
import org.project.bookingapi.exception.GlobalExceptionHandler;
import org.project.bookingapi.exception.ResourceNotFoundException;
import org.project.bookingapi.model.ApiError;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class GlobalExceptionHandlerTest {

    GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleConflict_buildsApiError() {
        BookingConflictException ex = new BookingConflictException("conflict here");
        var entity = handler.handleConflict(ex);

        assertThat(entity.getStatusCode().value()).isEqualTo(409);
        ApiError body = entity.getBody();
        assertThat(body).isNotNull();
        assertThat(body.status()).isEqualTo(409);
        assertThat(body.message()).isEqualTo("conflict here");
        assertThat(body.timestamp()).isNotNull();
    }

    @Test
    void handleNotFound_buildsApiError() {
        ResourceNotFoundException ex = new ResourceNotFoundException("not found");
        var entity = handler.handleNotFound(ex);

        assertThat(entity.getStatusCode().value()).isEqualTo(404);
        ApiError body = entity.getBody();
        assertThat(body).isNotNull();
        assertThat(body.message()).isEqualTo("not found");
        assertThat(body.status()).isEqualTo(404);
    }

    @Test
    void handleAll_buildsInternalServerError() {
        Exception ex = new RuntimeException("boom");
        var entity = handler.handleAll(ex);

        assertThat(entity.getStatusCode().value()).isEqualTo(500);
        ApiError body = entity.getBody();
        assertThat(body).isNotNull();
        assertThat(body.message()).isEqualTo("boom");
        assertThat(body.status()).isEqualTo(500);
    }
}