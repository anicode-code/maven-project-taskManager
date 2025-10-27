package com.example.taskapi.service;

import com.example.taskapi.exception.ResourceNotFoundException;
import com.example.taskapi.model.Task;
import com.example.taskapi.model.User;
import com.example.taskapi.repository.TaskRepository;
import com.example.taskapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository repo;
    private final UserRepository userRepository;

    public List<Task> findAllForUsername(String username) {
        return repo.findByUserUsername(username);
    }

    public Task findByIdAndUsername(Long id, String username) {
        return repo.findById(id)
                .filter(t -> t.getUser() != null && username.equals(t.getUser().getUsername()))
                .orElseThrow(() -> new ResourceNotFoundException("Task not found for user"));
    }

    public Task createForUsername(String username, Task t) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        t.setId(null);
        t.setUser(user);
        return repo.save(t);
    }

    public Task updateForUsername(Long id, String username, Task updated) {
        Task t = findByIdAndUsername(id, username);
        t.setTitle(updated.getTitle());
        t.setDescription(updated.getDescription());
        t.setCompleted(updated.isCompleted());
        return repo.save(t);
    }

    public void deleteForUsername(Long id, String username) {
        Task t = findByIdAndUsername(id, username);
        repo.delete(t);
    }
}
