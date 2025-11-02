package com.example.taskapi.unit.controllers;

import com.example.taskapi.controller.TaskController;
import com.example.taskapi.model.Task;
import com.example.taskapi.service.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.security.Principal;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class TaskControllerUnitTest {

    @Mock TaskService taskService;
    @InjectMocks TaskController taskController;

    @BeforeEach
    void setUp() { MockitoAnnotations.openMocks(this); }

    private Principal p(String name) { return () -> name; }

    @Test
    void getAllTasks_ok() {
        when(taskService.getAllTasksForUser("alice")).thenReturn(List.of(new Task(1L, "a", "d", false, null)));
        var resp = taskController.getAllTasks(p("alice"));
        assertThat(resp.getStatusCode().value()).isEqualTo(200);
        verify(taskService).getAllTasksForUser("alice");
    }

    @Test
    void createTask_ok() {
        Task t = new Task(null, "t", "d", false, null);
        when(taskService.createTask(eq("bob"), any(Task.class))).thenAnswer(i -> {
            Task in = i.getArgument(1);
            in.setId(5L);
            return in;
        });
        var resp = taskController.createTask(t, p("bob"));
        assertThat(resp.getStatusCode().value()).isEqualTo(201);
        assertThat(((Task) resp.getBody()).getId()).isEqualTo(5L);
    }

    @Test
    void update_and_delete_ok() {
        Task updated = new Task(null, "up", "desc", true, null);
        when(taskService.updateTask("sam", 2L, updated)).thenReturn(new Task(2L, "up", "desc", true, null));
        var resp = taskController.updateTask(2L, updated, p("sam"));
        assertThat(resp.getStatusCode().value()).isEqualTo(200);

        doNothing().when(taskService).deleteTask("sam", 2L);
        var delResp = taskController.deleteTask(2L, p("sam"));
        assertThat(delResp.getStatusCode().value()).isEqualTo(204);
    }
}
