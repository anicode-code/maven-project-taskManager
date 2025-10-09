package com.example.taskapi.service;

import com.example.taskapi.exception.ResourceNotFoundException;
import com.example.taskapi.model.Task;
import com.example.taskapi.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository repo;

    public Task create(Task task) {
        return repo.save(task);
    }

    public List<Task> findAll() {
        return repo.findAll();
    }

    public Task findById(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task", "Task not found with id " + id));
    }

    public Task update(Long id, Task updated) {
        Task t = findById(id);
        t.setTitle(updated.getTitle());
        t.setDescription(updated.getDescription());
        t.setCompleted(updated.isCompleted());
        return repo.save(t);
    }

    public void delete(Long id) {
        Task t = findById(id);
        repo.delete(t);
    }
}
