package com.example.taskapi.service;

import com.example.taskapi.exception.ResourceNotFoundException;
import com.example.taskapi.model.Task;
import com.example.taskapi.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository repo;

    @InjectMocks
    private TaskService service;

    private Task task1;
    private Task task2;

    @BeforeEach
    void setUp() {
        task1 = new Task(1L, "T1", "Desc1", false);
        task2 = new Task(2L, "T2", "Desc2", true);
    }

    @Test
    void create_shouldSaveAndReturnTask() {
        when(repo.save(any(Task.class))).thenReturn(task1);

        Task input = new Task(null, "T1", "Desc1", false);
        Task saved = service.create(input);

        assertThat(saved).isNotNull();
        assertThat(saved.getId()).isEqualTo(1L);
        assertThat(saved.getTitle()).isEqualTo("T1");

        ArgumentCaptor<Task> captor = ArgumentCaptor.forClass(Task.class);
        verify(repo, times(1)).save(captor.capture());
        assertThat(captor.getValue().getTitle()).isEqualTo("T1");
    }

    @Test
    void findAll_shouldReturnList() {
        when(repo.findAll()).thenReturn(List.of(task1, task2));

        List<Task> all = service.findAll();

        assertThat(all).hasSize(2).containsExactly(task1, task2);
        verify(repo, times(1)).findAll();
    }

    @Test
    void findById_existingId_shouldReturnTask() {
        when(repo.findById(1L)).thenReturn(Optional.of(task1));

        Task t = service.findById(1L);

        assertThat(t).isEqualTo(task1);
        verify(repo).findById(1L);
    }

    @Test
    void findById_missingId_shouldThrow() {
        when(repo.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Task not found with id");

        verify(repo).findById(99L);
    }

    @Test
    void update_existing_shouldModifyAndSave() {
        when(repo.findById(1L)).thenReturn(Optional.of(task1));
        when(repo.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Task updated = new Task(null, "Updated", "NewDesc", true);
        Task result = service.update(1L, updated);

        assertThat(result.getTitle()).isEqualTo("Updated");
        assertThat(result.isCompleted()).isTrue();
        verify(repo).findById(1L);
        verify(repo).save(task1);
    }

    @Test
    void delete_existing_shouldCallDelete() {
        when(repo.findById(1L)).thenReturn(Optional.of(task1));

        service.delete(1L);

        verify(repo).delete(task1);
    }

    @Test
    void delete_missing_shouldThrow() {
        when(repo.findById(5L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.delete(5L))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(repo).findById(5L);
        verify(repo, never()).delete(any());
    }
}
