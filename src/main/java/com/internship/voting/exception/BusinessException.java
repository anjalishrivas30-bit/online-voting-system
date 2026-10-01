package com.internship.voting.exception;

/** Thrown when a business rule is violated (double vote, closed election, unapproved voter ...). */
public class BusinessException extends RuntimeException {
    public BusinessException(String message) {
        super(message);
    }
}
