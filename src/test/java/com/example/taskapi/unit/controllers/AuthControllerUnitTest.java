package com.example.taskapi.unit.controllers;

import com.example.taskapi.controller.AuthController;
import com.example.taskapi.model.User;
import com.example.taskapi.repository.UserRepository;
import com.example.taskapi.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.security.Principal;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Updated unit tests for AuthController focusing on:
 * - me (success + unauthenticated + not found)
 * - updatePassword (success + missing fields + wrong old password + unauthenticated + not found)
 * - delete (success + unauthenticated + not found)
 *
 * Path: src/test/java/com/example/taskapi/unit/controllers/AuthControllerUnitTest.java
 */
class AuthControllerUnitTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtUtil jwtUtil;

    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private AuthController authController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        authController = new AuthController(userRepository, passwordEncoder, jwtUtil);

        // Default save behaviour: return same user but ensure id is set if missing
        when(userRepository.save(any(User.class))).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            if (u.getId() == null) u.setId(1L);
            return u;
        });
    }

    private Principal p(String name) {
        return () -> name;
    }

    // ---------- me tests ----------

    @Test
    void me_success_returnsUserInfo() {
        User u = new User(11L, "meuser", "pw");
        when(userRepository.findByUsername("meuser")).thenReturn(Optional.of(u));

        ResponseEntity<?> resp = authController.me(p("meuser"));
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);

        var body = (Map<?, ?>) resp.getBody();
        assertThat(body.get("username")).isEqualTo("meuser");
        assertThat(body.get("id")).isEqualTo(11L);
    }

    @Test
    void me_unauthenticated_returns401() {
        ResponseEntity<?> resp = authController.me(null);
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        var b = (Map<?, ?>) resp.getBody();
        assertThat(b.get("error")).isEqualTo("unauthenticated");
    }

    @Test
    void me_userNotFound_returns404() {
        when(userRepository.findByUsername("ghost")).thenReturn(Optional.empty());
        ResponseEntity<?> resp = authController.me(p("ghost"));
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        var b = (Map<?, ?>) resp.getBody();
        assertThat(b.get("error")).isEqualTo("not_found");
    }

    // ---------- updatePassword tests ----------

    @Test
    void updatePassword_success_changesPasswordAndReturns200() {
        String old = "oldpw";
        String newPw = "newpw";
        String encodedOld = passwordEncoder.encode(old);
        User u = new User(20L, "changeme", encodedOld);

        when(userRepository.findByUsername("changeme")).thenReturn(Optional.of(u));
        // save returns the updated user
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        Map<String, String> body = Map.of("oldPassword", old, "newPassword", newPw);
        ResponseEntity<?> resp = authController.updatePassword(body, p("changeme"));

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
        var b = (Map<?, ?>) resp.getBody();
        assertThat(b.get("message")).isEqualTo("password_updated");

        // verify that repository.save was called with the new encoded password
        verify(userRepository).save(argThat(saved -> passwordEncoder.matches(newPw, saved.getPassword())));
    }

    @Test
    void updatePassword_missingFields_returns400() {
        // missing newPassword
        Map<String, String> body = Map.of("oldPassword", "x");
        ResponseEntity<?> resp = authController.updatePassword(body, p("changeme"));
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        var b = (Map<?, ?>) resp.getBody();
        assertThat(b.get("error")).isEqualTo("missing_fields");
    }

    @Test
    void updatePassword_wrongOldPassword_returns400() {
        String old = "oldpw";
        String encodedOld = passwordEncoder.encode(old);
        User u = new User(21L, "userX", encodedOld);

        when(userRepository.findByUsername("userX")).thenReturn(Optional.of(u));

        Map<String, String> body = Map.of("oldPassword", "wrong", "newPassword", "np");
        ResponseEntity<?> resp = authController.updatePassword(body, p("userX"));
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        var b = (Map<?, ?>) resp.getBody();
        assertThat(b.get("error")).isEqualTo("wrong_old_password");
    }

    @Test
    void updatePassword_unauthenticated_returns401() {
        Map<String, String> body = Map.of("oldPassword", "a", "newPassword", "b");
        ResponseEntity<?> resp = authController.updatePassword(body, null);
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        var b = (Map<?, ?>) resp.getBody();
        assertThat(b.get("error")).isEqualTo("unauthenticated");
    }

    @Test
    void updatePassword_userNotFound_returns404() {
        when(userRepository.findByUsername("missing")).thenReturn(Optional.empty());
        Map<String, String> body = Map.of("oldPassword", "o", "newPassword", "n");
        ResponseEntity<?> resp = authController.updatePassword(body, p("missing"));
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        var b = (Map<?, ?>) resp.getBody();
        assertThat(b.get("error")).isEqualTo("not_found");
    }

    // ---------- delete tests ----------

    @Test
    void delete_success_deletesUserAndReturns200() {
        User u = new User(99L, "delme", "pw");
        when(userRepository.findByUsername("delme")).thenReturn(Optional.of(u));
        doNothing().when(userRepository).delete(u);

        ResponseEntity<?> resp = authController.delete(p("delme"));
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
        var b = (Map<?, ?>) resp.getBody();
        assertThat(b.get("message")).isEqualTo("user_deleted");
        verify(userRepository).delete(u);
    }

    @Test
    void delete_unauthenticated_returns401() {
        ResponseEntity<?> resp = authController.delete(null);
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        var b = (Map<?, ?>) resp.getBody();
        assertThat(b.get("error")).isEqualTo("unauthenticated");
    }

    @Test
    void delete_userNotFound_returns404() {
        when(userRepository.findByUsername("nobody")).thenReturn(Optional.empty());
        ResponseEntity<?> resp = authController.delete(p("nobody"));
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        var b = (Map<?, ?>) resp.getBody();
        assertThat(b.get("error")).isEqualTo("not_found");
    }
}
