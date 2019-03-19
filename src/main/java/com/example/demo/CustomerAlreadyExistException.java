package com.example.demo;

public class CustomerAlreadyExistException extends RuntimeException {

    public CustomerAlreadyExistException(CustomerModel customer) {
        super(String.format("User %s exist in DB", customer));
    }
}
