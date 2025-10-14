package com.example.taskapi.repository;

import com.example.taskapi.model.Task;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class TaskRepositoryIntegrationTest {

    @Autowired
    private TaskRepository repository;

    @Test
    void saveAndFindById_andDelete_shouldWork() {
        Task t = new Task(null, "RepoTest", "from repo", false);
        Task saved = repository.save(t);

        assertThat(saved.getId()).isNotNull();
        Long id = saved.getId();

        Optional<Task> found = repository.findById(id);
        assertThat(found).isPresent();
        assertThat(found.get().getTitle()).isEqualTo("RepoTest");

        // update and flush
        saved.setCompleted(true);
        repository.save(saved);

        Task updated = repository.findById(id).orElseThrow();
        assertThat(updated.isCompleted()).isTrue();

        // delete
        repository.delete(updated);
        assertThat(repository.findById(id)).isEmpty();
    }
}
