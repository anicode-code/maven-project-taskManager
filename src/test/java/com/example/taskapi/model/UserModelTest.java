package com.example.taskapi.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserModelTest {

    @Test
    void lombok_gettersSetters_andEquals() {
        User u = new User(1L, "alice", "secret");
        assertThat(u.getId()).isEqualTo(1L);
        assertThat(u.getUsername()).isEqualTo("alice");
        assertThat(u.getPassword()).isEqualTo("secret");

        u.setPassword("newpass");
        assertThat(u.getPassword()).isEqualTo("newpass");

        User u2 = new User(1L, "alice", "newpass");
        assertThat(u).isEqualTo(u2);
    }
}
