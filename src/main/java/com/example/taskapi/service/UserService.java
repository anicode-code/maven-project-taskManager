package com.example.taskapi.service;

import com.example.taskapi.exception.ResourceNotFoundException;
import com.example.taskapi.model.User;
import com.example.taskapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repo;

    public User create(User user) {
        // simple uniqueness check
        if (repo.existsByUsername(user.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }
        return repo.save(user);
    }

    public List<User> findAll() {
        return repo.findAll();
    }

    public User findById(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + id));
    }

    public User findByUsername(String username) {
        return repo.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username " + username));
    }

    public User update(Long id, User updated) {
        User u = findById(id);
        u.setUsername(updated.getUsername());
        u.setPassword(updated.getPassword());
        return repo.save(u);
    }

    public void delete(Long id) {
        User u = findById(id);
        repo.delete(u);
    }
}
