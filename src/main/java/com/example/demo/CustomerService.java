package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;



    Optional<CustomerModel> findCustomerByPesel(String pesel) {
        List<CustomerDBModel> customerList = customerRepository.findByPesel(pesel);
        if(customerList.isEmpty()){
            return Optional.empty();
        }
        if(customerList.size() > 1){
            throw new ToManyCustomerException(pesel);
        }
        return Optional.of(customerList.get(0).toDomain());
    }

    List<CustomerModel> getAllCustomers() {
        return customerRepository.findAll().stream().map(CustomerDBModel::toDomain).collect(Collectors.toList());
    }

    void addCustomer(CustomerModel customer){
        List<CustomerDBModel> customerDBModelList = customerRepository.findByPesel(customer.getPesel());
        if(!customerDBModelList.isEmpty()){
            throw new CustomerAlreadyExistException(customer);
        }
        customerRepository.save(CustomerDBModel.from(customer));
    }

    void deleteCustomer(String pesel){
        List<CustomerDBModel> list = customerRepository.findByPesel(pesel);
        if(list.isEmpty()){
            return;
        }
        if(list.size() > 1){
            throw new ToManyCustomerException(pesel);
        }
        customerRepository.delete(list.get(0));
    }

    void updateCustomer(CustomerModel customerBefore, CustomerModel customerAfter) {
        List<CustomerDBModel> customerDBModelList = customerRepository.findByPesel(customerBefore.getPesel());
        if(customerDBModelList.isEmpty())
            throw new CustomerNotExistException(customerBefore);
        if(customerDBModelList.size() > 1)
            throw new ToManyCustomerException(customerBefore.getPesel());
        customerRepository.save(CustomerDBModel.from(customerAfter));
    }


//  Predicate<CustomerModel> filter
}
