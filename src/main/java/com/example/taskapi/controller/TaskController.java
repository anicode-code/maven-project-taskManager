package com.example.taskapi.controller;

import com.example.taskapi.model.Task;
import com.example.taskapi.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @GetMapping
    public ResponseEntity<?> getAllTasks(Principal principal) {
        if (principal == null) return ResponseEntity.status(401).body(Map.of("error", "unauthorized"));
        List<Task> tasks = taskService.getAllTasksForUser(principal.getName());
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getTaskById(@PathVariable Long id, Principal principal) {
        if (principal == null) return ResponseEntity.status(401).body(Map.of("error", "unauthorized"));
        Task task = taskService.getTaskById(principal.getName(), id);
        return ResponseEntity.ok(task);
    }

    @PostMapping
    public ResponseEntity<?> createTask(@RequestBody Task task, Principal principal) {
        if (principal == null) return ResponseEntity.status(401).body(Map.of("error", "unauthorized"));
        Task created = taskService.createTask(principal.getName(), task);
        return ResponseEntity.status(201).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateTask(@PathVariable Long id, @RequestBody Task updatedTask, Principal principal) {
        if (principal == null) return ResponseEntity.status(401).body(Map.of("error", "unauthorized"));
        Task updated = taskService.updateTask(principal.getName(), id, updatedTask);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTask(@PathVariable Long id, Principal principal) {
        if (principal == null) return ResponseEntity.status(401).body(Map.of("error", "unauthorized"));
        taskService.deleteTask(principal.getName(), id);
        return ResponseEntity.noContent().build();
    }
}
