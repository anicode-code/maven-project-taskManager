package com.example.taskapi.controller;

import com.example.taskapi.model.Task;
import com.example.taskapi.service.TaskService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;

import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(controllers = TaskController.class)
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TaskService service;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createTask_shouldReturnCreated() throws Exception {
        Task toCreate = new Task(null, "Buy", "desc", false);
        Task created = new Task(1L, "Buy", "desc", false);

        Mockito.when(service.create(any(Task.class))).thenReturn(created);

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(toCreate)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.title", is("Buy")));
    }

    @Test
    void getAllTasks_shouldReturnList() throws Exception {
        Task t1 = new Task(1L, "A", "a", false);
        Task t2 = new Task(2L, "B", "b", true);

        Mockito.when(service.findAll()).thenReturn(List.of(t1, t2));

        mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].title", is("A")))
                .andExpect(jsonPath("$[1].completed", is(true)));
    }

    @Test
    void getTaskById_shouldReturnTask() throws Exception {
        Task t = new Task(1L, "Single", "one", false);
        Mockito.when(service.findById(1L)).thenReturn(t);

        mockMvc.perform(get("/api/tasks/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is("Single")))
                .andExpect(jsonPath("$.id", is(1)));
    }

    @Test
    void updateTask_shouldReturnUpdated() throws Exception {
        Task incoming = new Task(null, "Upd", "new", true);
        Task updated = new Task(1L, "Upd", "new", true);

        Mockito.when(service.update(eq(1L), any(Task.class))).thenReturn(updated);

        mockMvc.perform(put("/api/tasks/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(incoming)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.completed", is(true)));
    }

    @Test
    void deleteTask_shouldReturnNoContent() throws Exception {
        Mockito.doNothing().when(service).delete(1L);

        mockMvc.perform(delete("/api/tasks/{id}", 1L))
                .andExpect(status().isNoContent());
    }
}
