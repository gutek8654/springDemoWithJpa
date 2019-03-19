package com.example.demo;

import lombok.val;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class Runner implements CommandLineRunner {
    private static final Logger logger = LoggerFactory.getLogger(Runner.class);

    @Autowired
    CustomerRepository customerRepository;

    @Override
    public void run(String...args) throws Exception {
        customerRepository.save(new CustomerDBModel("Jan", "Kowalski", "123"));
        customerRepository.save(new CustomerDBModel("Kasia", "Kowalska", "124"));
        customerRepository.save(new CustomerDBModel("Basia", "Nowak", "125"));

        System.out.println("List of customer is:");
        for (val c: customerRepository.findAll()){
            System.out.println(c);
        }

    }
}