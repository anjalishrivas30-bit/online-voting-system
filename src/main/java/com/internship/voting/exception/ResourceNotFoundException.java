package com.internship.voting.exception;

/** Thrown when an entity (election, candidate, user) does not exist. Rendered as the custom 404 page. */
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
