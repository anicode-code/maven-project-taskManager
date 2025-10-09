package com.example.taskapi.controller;

import com.example.taskapi.model.Task;
import com.example.taskapi.service.TaskService;

// import org.apache.commons.logging.Log;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
// @Slf4j
public class TaskController {

    private final TaskService service;

    public TaskController(TaskService service) {
        this.service = service;
    }

    // Create
    @PostMapping
    public ResponseEntity<Task> createTask(@RequestBody Task task) {
        // log.info(task.toString());
        Task created = service.create(task);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    // Get all
    @GetMapping
    public List<Task> getAllTasks() {
        return service.findAll();
    }

    // Get by id
    @GetMapping("/{id}")
    public ResponseEntity<Task> getTaskById(@PathVariable Long id) {
        Task task = service.findById(id);
        return ResponseEntity.ok(task);
    }

    // Update
    @PutMapping("/{id}")
    public ResponseEntity<Task> updateTask(@PathVariable Long id, @RequestBody Task task) {
        Task updated = service.update(id, task);
        return ResponseEntity.ok(updated);
    }

    // Delete
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
