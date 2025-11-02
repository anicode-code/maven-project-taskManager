package com.example.taskapi.integration.app;

import com.example.taskapi.TaskApiApplication;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = TaskApiApplication.class)
public class TaskApiApplicationContextTest {
    @Test void contextLoads() { /* passes if context starts */ }
}
