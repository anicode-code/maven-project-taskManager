package com.example.taskapi.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ResourceNotFoundExceptionTest {

    @Test
    void constructors_and_getter_shouldWork() {
        ResourceNotFoundException ex1 = new ResourceNotFoundException("Task not found");
        assertThat(ex1.getMessage()).isEqualTo("Task not found");
        assertThat(ex1.getResourceName()).isNull();

        ResourceNotFoundException ex2 = new ResourceNotFoundException("Task", "Not found for id 1");
        assertThat(ex2.getMessage()).isEqualTo("Not found for id 1");
        assertThat(ex2.getResourceName()).isEqualTo("Task");
    }
}
