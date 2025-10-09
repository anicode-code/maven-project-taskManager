package com.example.taskapi.exception;

import lombok.Getter;

@Getter
public class ResourceNotFoundException extends RuntimeException {
    private final String resourceName;

    public ResourceNotFoundException(String message) {
        super(message);
        this.resourceName = null;
    }

    public ResourceNotFoundException(String resourceName, String message) {
        super(message);
        this.resourceName = resourceName;
    }
}
