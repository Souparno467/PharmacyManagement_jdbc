package com.example.exceptions;

public class DatabaseOperationException extends Exception { // Custom exception for database operation errors
    public DatabaseOperationException(String message, Throwable cause) {
        super(message, cause);
    }
    public DatabaseOperationException(String message) {
        super(message);
    }
}
