package com.example.demo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerDBModel {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String firstName;

    private String lastName;

    private String pesel;

    public CustomerDBModel(String firstName, String lastName, String pesel) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.pesel = pesel;
    }

    //adapter pattern - adapters 'from' and 'to'
    static CustomerDBModel from(CustomerModel c){
        return new CustomerDBModel(c.getFirstName(), c.getLastName(), c.getPesel());
    }

    CustomerModel toDomain(){
        return new CustomerModel(this.firstName, this.lastName, this.pesel);
    }
}
