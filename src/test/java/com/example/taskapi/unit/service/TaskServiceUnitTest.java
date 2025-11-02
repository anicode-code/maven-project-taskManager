package com.example.taskapi.unit.service;

import com.example.taskapi.exception.ResourceNotFoundException;
import com.example.taskapi.model.Task;
import com.example.taskapi.model.User;
import com.example.taskapi.repository.TaskRepository;
import com.example.taskapi.repository.UserRepository;
import com.example.taskapi.service.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TaskServiceUnitTest {

    @Mock TaskRepository taskRepository;
    @Mock UserRepository userRepository;
    @InjectMocks TaskService taskService;

    @BeforeEach
    void setUp() { MockitoAnnotations.openMocks(this); }

    @Test
    void getAllTasksForUser_returnsTasks() {
        var user = new User(1L, "alice", "hash");
        var t1 = new Task(1L, "T1", "d1", false, user);
        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(user));
        when(taskRepository.findByUser(user)).thenReturn(List.of(t1));

        var tasks = taskService.getAllTasksForUser("alice");
        assertThat(tasks).hasSize(1).first().extracting(Task::getTitle).isEqualTo("T1");
    }

    @Test
    void getTaskById_notOwned_throws() {
        var user = new User(2L, "bob", "h");
        var other = new User(3L, "eve", "h");
        var t = new Task(11L, "X", "Y", false, other);
        when(userRepository.findByUsername("bob")).thenReturn(Optional.of(user));
        when(taskRepository.findById(11L)).thenReturn(Optional.of(t));

        assertThatThrownBy(() -> taskService.getTaskById("bob", 11L))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void create_update_delete_roundtrip() {
        var user = new User(5L, "sam", "h");
        when(userRepository.findByUsername("sam")).thenReturn(Optional.of(user));

        // When saving, return the passed Task but ensure it gets an id if missing
        when(taskRepository.save(any(Task.class))).thenAnswer(inv -> {
            Task s = inv.getArgument(0);
            if (s == null) throw new IllegalArgumentException("Task argument was null");
            if (s.getId() == null) s.setId(99L);
            return s;
        });

        // CREATE
        var created = taskService.createTask("sam", new Task(null, "n", "d", false, null));
        assertThat(created.getId()).isEqualTo(99L);
        assertThat(created.getUser()).isEqualTo(user);

        // PREPARE stored state for update/delete
        var stored = new Task(99L, "n", "d", false, user);
        when(taskRepository.findById(99L)).thenReturn(Optional.of(stored));
        when(taskRepository.save(any(Task.class))).thenAnswer(inv -> inv.getArgument(0));

        // UPDATE
        var updated = taskService.updateTask("sam", 99L, new Task(null, "u", "d2", true, null));
        assertThat(updated.getTitle()).isEqualTo("u");
        assertThat(updated.isCompleted()).isTrue();

        // DELETE
        taskService.deleteTask("sam", 99L);
        verify(taskRepository).delete(stored);
    }
}
