package com.example.demo;

public class CustomerNotExistException extends RuntimeException {

    public CustomerNotExistException(CustomerModel customer) {
        super(String.format("Customer %s not exist", customer));
    }
}
