package com.example.taskapi.integration.repository;

import com.example.taskapi.model.User;
import com.example.taskapi.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
public class UserRepositoryIntegrationTest {

    @Autowired UserRepository userRepository;

    @Test
    void saveAndFindByUsername() {
        User u = new User();
        u.setUsername("repoUser");
        u.setPassword("pw");
        userRepository.save(u);

        var found = userRepository.findByUsername("repoUser");
        assertThat(found).isPresent();
        assertThat(found.get().getUsername()).isEqualTo("repoUser");
    }
}
