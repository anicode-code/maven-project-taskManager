package com.example.taskapi.controller;

import com.example.taskapi.model.Task;
import com.example.taskapi.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService service;

    // List tasks for authenticated user
    @GetMapping
    public ResponseEntity<List<Task>> getTasks(Authentication auth) {
        String username = auth.getName();
        return ResponseEntity.ok(service.findAllForUsername(username));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Task> getTask(@PathVariable Long id, Authentication auth) {
        String username = auth.getName();
        Task t = service.findByIdAndUsername(id, username);
        return ResponseEntity.ok(t);
    }

    @PostMapping
    public ResponseEntity<Task> createTask(@RequestBody Task task, Authentication auth) {
        String username = auth.getName();
        Task created = service.createForUsername(username, task);
        return ResponseEntity.status(201).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Task> updateTask(@PathVariable Long id, @RequestBody Task task, Authentication auth) {
        String username = auth.getName();
        Task updated = service.updateForUsername(id, username, task);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id, Authentication auth) {
        String username = auth.getName();
        service.deleteForUsername(id, username);
        return ResponseEntity.noContent().build();
    }
}
