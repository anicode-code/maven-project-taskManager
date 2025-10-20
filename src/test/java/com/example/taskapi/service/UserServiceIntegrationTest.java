package com.example.taskapi.service;

import com.example.taskapi.exception.ResourceNotFoundException;
import com.example.taskapi.model.User;
import com.example.taskapi.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
class UserServiceIntegrationTest {

    @Autowired
    private UserService service;

    @Autowired
    private UserRepository repo;

    @Test
    void create_findAll_findById_update_delete() {
        User in = new User(null, "dave", "pwd");
        User created = service.create(in);

        assertThat(created.getId()).isNotNull();
        Long id = created.getId();

        List<User> all = service.findAll();
        assertThat(all).isNotEmpty();

        User found = service.findById(id);
        assertThat(found.getUsername()).isEqualTo("dave");

        User updatedPayload = new User(null, "dave2", "pwd2");
        User updated = service.update(id, updatedPayload);
        assertThat(updated.getUsername()).isEqualTo("dave2");
        assertThat(updated.getPassword()).isEqualTo("pwd2");

        service.delete(id);
        assertThat(repo.findById(id)).isEmpty();
    }

    @Test
    void findById_nonExisting_shouldThrow() {
        assertThatThrownBy(() -> service.findById(9999L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void create_duplicateUsername_shouldThrow() {
        User u = new User(null, "eve", "p");
        service.create(u);

        User dup = new User(null, "eve", "p2");
        assertThatThrownBy(() -> service.create(dup)).isInstanceOf(IllegalArgumentException.class);
    }
}
