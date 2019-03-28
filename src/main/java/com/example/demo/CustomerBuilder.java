package com.example.demo;

public class CustomerBuilder {

    private CustomerModel customer;

    public CustomerModel build(){
        return customer;
    }

    public CustomerBuilder(){
        customer = new CustomerModel();
    }

    public CustomerBuilder setFirstName(String firstName){
        customer.setFirstName(firstName);
        return this;
    }

    public CustomerBuilder setLastName(String lastName){
        customer.setLastName(lastName);
        return this;
    }

    public CustomerBuilder setPesel(String pesel){
        customer.setPesel(pesel);
        return this;
    }
}
