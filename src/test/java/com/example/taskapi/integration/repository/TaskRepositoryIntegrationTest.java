package com.example.taskapi.integration.repository;

import com.example.taskapi.model.Task;
import com.example.taskapi.model.User;
import com.example.taskapi.repository.TaskRepository;
import com.example.taskapi.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
public class TaskRepositoryIntegrationTest {

    @Autowired TaskRepository taskRepository;
    @Autowired UserRepository userRepository;

    @Test
    void saveTask_and_findByUser() {
        User u = new User();
        u.setUsername("ruser");
        u.setPassword("pw");
        userRepository.save(u);

        Task t = new Task();
        t.setTitle("rtitle");
        t.setUser(u);
        taskRepository.save(t);

        List<Task> tasks = taskRepository.findByUser(u);
        assertThat(tasks).hasSize(1);
        assertThat(tasks.get(0).getTitle()).isEqualTo("rtitle");
    }
}
