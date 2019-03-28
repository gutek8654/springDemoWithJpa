package com.example.demo;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(CustomerController.class)
class CustomerControllerIT {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper mapper;

    @MockBean
    CustomerRepository customerRepository;

    CustomerBuilder customerBuilder = new CustomerBuilder();

    @Test
    void addCustomer() throws Exception {
        // given
        CustomerDBModel customer = CustomerDBModel.from(customerBuilder.setFirstName("Jane")
                .setLastName("Kowalski")
                .setPesel("123")
                .build());
        when(customerRepository.save(customer)).thenReturn(null);

        // when
        // then
        mockMvc.perform(
                post("/customers")
                        .contentType("application/json;charset=UTF-8")
                        .content(mapper.writeValueAsBytes(customer)))
                .andDo(print())
                .andExpect(status().isOk());
        verify(customerRepository).save(customer);

    }

    @Test
    void getCustomersTest() throws Exception {
        // given
        List<CustomerDBModel> list = Arrays.asList(
                CustomerDBModel.from(customerBuilder.setFirstName("Jan")
                        .setLastName("Kowalski")
                        .setPesel("123")
                        .build()),
                CustomerDBModel.from(customerBuilder.setFirstName("Tomasz")
                        .setLastName("Kowalski")
                        .setPesel("124")
                        .build())
        );
        when(customerRepository.findAll()).thenReturn(list);

        // when
        // then
        mockMvc.perform(get("/customers"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(content().json(mapper.writeValueAsString(list)));

    }
}