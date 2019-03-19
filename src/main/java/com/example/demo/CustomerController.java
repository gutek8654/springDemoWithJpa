package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/customers")
public class CustomerController {

    @Autowired
    private CustomerRepository customerRepository;

    @RequestMapping(method = RequestMethod.POST)
    public void addCustomer(@RequestBody CustomerDBModel customerDBModel){
        customerRepository.save(customerDBModel);
    }

    @RequestMapping(method = RequestMethod.GET)
    public List<CustomerDBModel> getCustomers(){
        return customerRepository.findAll();
    }
}
