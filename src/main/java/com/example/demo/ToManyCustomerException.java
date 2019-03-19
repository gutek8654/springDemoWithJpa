package com.example.demo;

import lombok.Value;

@Value
public class ToManyCustomerException extends RuntimeException {

    String pesel;

    public ToManyCustomerException(String pesel) {
        super(String.format("More than one customer found with pesel: %s", pesel));
        this.pesel = pesel;
    }
}
