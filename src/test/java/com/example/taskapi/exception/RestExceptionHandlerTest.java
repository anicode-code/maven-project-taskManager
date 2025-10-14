package com.example.taskapi.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import java.time.Instant;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RestExceptionHandlerTest {

    private RestExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new RestExceptionHandler();
    }

    @Test
    void handleNotFound_shouldReturn404_andMapBodyWithMessageAndTimestamp() {
        ResourceNotFoundException ex = new ResourceNotFoundException("Task not found with id 1");
        ResponseEntity<Object> resp = handler.handleNotFound(ex);

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(resp.getBody()).isInstanceOf(Map.class);

        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) resp.getBody();

        assertThat(body).containsKeys("timestamp", "message");
        assertThat(body.get("message")).isEqualTo("Task not found with id 1");
        assertThat(body.get("timestamp")).isInstanceOf(Instant.class);
    }

    @Test
    void handleOtherExceptions_withMessage_shouldReturn500_andUseMessage() {
        Exception ex = new RuntimeException("boom");
        ResponseEntity<Object> resp = handler.handleOtherExceptions(ex);

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(resp.getBody()).isInstanceOf(Map.class);

        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) resp.getBody();

        assertThat(body.get("message")).isEqualTo("boom");
        assertThat(body.get("timestamp")).isInstanceOf(Instant.class);
    }

    @Test
    void handleOtherExceptions_withNullMessage_shouldReturn500_andUseFallback() {
        Exception ex = new RuntimeException((String) null);
        ResponseEntity<Object> resp = handler.handleOtherExceptions(ex);

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(resp.getBody()).isInstanceOf(Map.class);

        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) resp.getBody();

        assertThat(body.get("message")).isEqualTo("Unexpected error");
        assertThat(body.get("timestamp")).isInstanceOf(Instant.class);
    }

    @Test
    void handleResourceNotFoundException_shouldThrowUnsupportedOperationException() {
        // This explicitly covers the unimplemented method
        assertThrows(UnsupportedOperationException.class, () -> {
            handler.handleResourceNotFoundException(new ResourceNotFoundException("Task not found"));
        });
    }
}
