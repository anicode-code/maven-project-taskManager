package com.example.taskapi.unit.exception;

import com.example.taskapi.exception.GlobalExceptionHandler;
import com.example.taskapi.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.assertj.core.api.Assertions.*;

class GlobalExceptionHandlerUnitTest {

    GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void resourceNotFound_mapsTo404() {
        ResponseEntity<?> resp = handler.handleNotFound(new ResourceNotFoundException("x"));
        assertThat(resp.getStatusCode().value()).isEqualTo(404);
        assertThat(((Map<?,?>)resp.getBody()).get("error")).isEqualTo("not_found");
    }
}
