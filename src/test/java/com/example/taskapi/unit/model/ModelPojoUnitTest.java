package com.example.taskapi.unit.model;

import com.example.taskapi.model.Task;
import com.example.taskapi.model.User;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class ModelPojoUnitTest {

    @Test
    void user_getters_setters() {
        User u = new User();
        u.setUsername("x");
        u.setPassword("p");
        assertThat(u.getUsername()).isEqualTo("x");
        assertThat(u.getPassword()).isEqualTo("p");
    }

    @Test
    void task_getters_setters_and_username_prop() {
        User u = new User(1L, "bob", "h");
        Task t = new Task();
        t.setTitle("T");
        t.setUser(u);
        assertThat(t.getTitle()).isEqualTo("T");
        assertThat(t.getUsername()).isEqualTo("bob");
    }
}
