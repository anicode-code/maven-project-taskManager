package com.example.taskapi;

import com.example.taskapi.controller.TaskController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SpringBootTest
class TaskApiApplicationTest {

    @Autowired
    private ApplicationContext ctx;

    @Autowired
    private TaskController taskController; // optional check that controller bean exists

    @Test
    void contextLoads_andBeansPresent() {
        // Spring context should be injected
        assertThat(ctx).isNotNull();

        // Verify TaskController bean is present in the context
        assertThat(taskController).isNotNull();
        assertThat(ctx.containsBean("taskController")).isTrue();
    }

    @Test
    void mainMethodRunsWithoutException() {
        // Ensures the main method can be invoked (smoke test)
        assertDoesNotThrow(() -> TaskApiApplication.main(new String[]{}));
    }
}
