package com.example.taskapi.service;

import com.example.taskapi.exception.ResourceNotFoundException;
import com.example.taskapi.model.Task;
import com.example.taskapi.repository.TaskRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
class TaskServiceIntegrationTest {

    @Autowired
    private TaskService service;

    @Autowired
    private TaskRepository repository;

    @Test
    void create_findAll_findById_update_and_delete() {
        // create
        Task toCreate = new Task(null, "ServiceTest", "svc", false);
        Task created = service.create(toCreate);

        assertThat(created.getId()).isNotNull();
        Long id = created.getId();

        // find all
        List<Task> all = service.findAll();
        assertThat(all).isNotEmpty();

        // findById
        Task found = service.findById(id);
        assertThat(found.getTitle()).isEqualTo("ServiceTest");

        // update
        Task updatedPayload = new Task(null, "ServiceTestUpdated", "svc2", true);
        Task updated = service.update(id, updatedPayload);
        assertThat(updated.getTitle()).isEqualTo("ServiceTestUpdated");
        assertThat(updated.isCompleted()).isTrue();

        // delete
        service.delete(id);
        assertThat(repository.findById(id)).isEmpty();
    }

    @Test
    void findById_nonExisting_shouldThrowResourceNotFoundException() {
        assertThatThrownBy(() -> service.findById(9999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Task not found with id");
    }
}
