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

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    public List<Task> getAllTasksForUser(String username) {
        User user = findUser(username);
        return taskRepository.findByUser(user);
    }

    public Task getTaskById(String username, Long id) {
        User user = findUser(username);
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));
        if (!task.getUser().getId().equals(user.getId())) {
            throw new ResourceNotFoundException("Task not found or not authorized");
        }
        return task;
    }

    public Task createTask(String username, Task task) {
        User user = findUser(username);
        task.setId(null);
        task.setUser(user);
        return taskRepository.save(task);
    }

    public Task updateTask(String username, Long id, Task updated) {
        Task task = getTaskById(username, id); // already checks ownership

        task.setTitle(updated.getTitle());
        task.setDescription(updated.getDescription());
        task.setCompleted(updated.isCompleted());

        return taskRepository.save(task);
    }

    public void deleteTask(String username, Long id) {
        Task task = getTaskById(username, id); // already checks ownership
        taskRepository.delete(task);
    }

    private User findUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }
}
