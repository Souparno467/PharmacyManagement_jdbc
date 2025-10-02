package com.example.exceptions;

public class MedicineNotFoundException extends RuntimeException { // Custom exception for handling cases where a medicine is not found
    public MedicineNotFoundException(String message) {
        super(message);
    }
}
