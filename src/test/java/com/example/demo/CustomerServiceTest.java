package com.example.demo;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith({SpringExtension.class, MockitoExtension.class})
class CustomerServiceTest {

    @MockBean
    CustomerRepository mockRepository;

    @InjectMocks
    CustomerService subject;

    CustomerBuilder customerBuilder = new CustomerBuilder();

    @Test
    void getCustomerByPeselReturnsOneItemTest() {
        // given
        String pesel = "123";
        CustomerModel expectedCustomer = customerBuilder.setFirstName("Jan")
                .setLastName("Kowalski")
                .setPesel(pesel)
                .build();
        List<CustomerDBModel> list = Arrays.asList(CustomerDBModel.from(expectedCustomer));
        Mockito.when(mockRepository.findByPesel(pesel)).thenReturn(list);

        // when
        Optional<CustomerModel> result = subject.findCustomerByPesel(pesel);

        // then
        assertThat(result).isPresent()
                .get()
                .isEqualTo(expectedCustomer);
    }

    @Test
    void getCustomerByPeselReturnsNullTest() {
        // given
        String pesel = "123";
        List<CustomerDBModel> list = Arrays.asList();
        Mockito.when(mockRepository.findByPesel(pesel)).thenReturn(list);

        // when
        Optional<CustomerModel> result = subject.findCustomerByPesel(pesel);

        // then
        assertThat(result).isNotPresent();
    }

    @Test
    void getCustomerByPeselReturnsMoreThanOneItemTest(){
        // given
        String pesel = "123";
        List<CustomerDBModel> list = Arrays.asList(
                CustomerDBModel.from(customerBuilder.setFirstName("Jan")
                    .setLastName("Kowalski")
                    .setPesel(pesel)
                    .build()),
                CustomerDBModel.from(customerBuilder.setFirstName("Adam")
                    .setLastName("Nowak")
                    .setPesel(pesel)
                    .build()));
        Mockito.when(mockRepository.findByPesel(pesel)).thenReturn(list);

        // when
        Throwable thrown = catchThrowable(() -> subject.findCustomerByPesel(pesel));

        // then
        assertThat(thrown).isInstanceOf(ToManyCustomerException.class)
                .hasMessage(String.format("More than one customer found with pesel: %s", pesel));
    }

    @ParameterizedTest
    @MethodSource("getAllConsumersProvider")
    void getAllCustomersTest(List<CustomerDBModel> expectedList) {
        // given
        Mockito.when(mockRepository.findAll()).thenReturn(expectedList);

        // when
        List<CustomerModel> actualList = subject.getAllCustomers();

        //then
        assertThat(actualList)
                .isEqualTo(expectedList.stream().map(CustomerDBModel::toDomain).collect(Collectors.toList()));
    }

    static Stream<Arguments> getAllConsumersProvider(){
        return Stream.of(
                arguments(Arrays.asList(
                        new CustomerDBModel("firstName1", "lastName1", "123"),
                        new CustomerDBModel("firstName2", "lastName2", "124"),
                        new CustomerDBModel("firstName3", "lastName3", "125"),
                        new CustomerDBModel("firstName4", "lastName4", "126"),
                        new CustomerDBModel("firstName5", "lastName5", "127"),
                        new CustomerDBModel("firstName6", "lastName6", "128"))),
                 arguments(Arrays.asList(
                        new CustomerDBModel("firstName1", "lastName1", "123"))),
                arguments(Arrays.asList()));
    }

    @Test
    void addCustomerTest() {
        // given
        String pesel = "123";
        CustomerDBModel expected = CustomerDBModel.from(customerBuilder.setFirstName("Jan")
                .setLastName("Kowalski")
                .setPesel(pesel)
                .build());
        Mockito.when(mockRepository.findByPesel(pesel)).thenReturn(Arrays.asList());

        // when
        subject.addCustomer(expected.toDomain());

        // then
        verify(mockRepository).save(expected);
    }

    @Test
    void addCustomerThatExistTest() {
        // given
        String pesel = "123";
        CustomerDBModel customer = CustomerDBModel.from(customerBuilder.setFirstName("Jan")
                .setLastName("Nowak")
                .setPesel(pesel)
                .build());
        Mockito.when(mockRepository.findByPesel(pesel)).thenReturn(Arrays.asList(customer));

        // when
        Throwable thrown = catchThrowable(() -> subject.addCustomer(customer.toDomain()));

        // then
        assertThat(thrown).isInstanceOf(CustomerAlreadyExistException.class)
                .hasMessage(String.format("User %s exist in DB", customer.toDomain()));
    }

    @Test
    void deleteCustomerThatExistTest() {
        // given
        String pesel = "123";
        CustomerDBModel customerDBModel = CustomerDBModel.from(customerBuilder.setFirstName("Jan")
                .setLastName("Nowak")
                .setPesel(pesel)
                .build());
        Mockito.when(mockRepository.findByPesel(pesel)).thenReturn(Arrays.asList(customerDBModel));

        // when
        subject.deleteCustomer(pesel);

        // then
        verify(mockRepository).findByPesel(pesel);
        verify(mockRepository).delete(customerDBModel);
    }

    @Test
    void deleteCustomerThatNotExistTest() {
        // given
        String pesel = "123";
        Mockito.when(mockRepository.findByPesel(pesel)).thenReturn(Arrays.asList());

        // when
        subject.deleteCustomer(pesel);

        // then
        verify(mockRepository).findByPesel(pesel);
        verifyNoMoreInteractions(mockRepository);
    }

    @Test
    void deleteCustomerThatExistMoreThanOnceTest() {
        // given
        String pesel = "123";
        Mockito.when(mockRepository.findByPesel(pesel)).thenReturn(Arrays.asList(
                CustomerDBModel.from(customerBuilder.setFirstName("Jan")
                        .setLastName("Nowak")
                        .setPesel(pesel)
                        .build()),
                CustomerDBModel.from(customerBuilder.setFirstName("Jan")
                        .setLastName("Nowak")
                        .setPesel(pesel)
                        .build())));

        // when
        Throwable thrown = catchThrowable(() -> subject.deleteCustomer(pesel));

        // then
        assertThat(thrown).isInstanceOf(ToManyCustomerException.class)
                .hasMessage(String.format("More than one customer found with pesel: %s", pesel));
    }

    @Test
    void updateCustomerWhichNotExistTest() {
        // given
        CustomerModel customerBefore =  customerBuilder.setFirstName("Jan")
                .setLastName("Nowak")
                .setPesel("123")
                .build();
        CustomerModel customerAfter = customerBuilder.setFirstName("Adan")
                .setLastName("Kowalski")
                .setPesel("124")
                .build();
        Mockito.when(mockRepository.findByPesel(customerBefore.getPesel())).thenReturn(Arrays.asList());

        // when
        Throwable thrown = catchThrowable(() -> subject.updateCustomer(customerBefore, customerAfter));

        // then
        assertThat(thrown).isInstanceOf(CustomerNotExistException.class)
                .hasMessage(String.format("Customer %s not exist", customerBefore));

    }

    @Test
    void updateCustomerThatExistTest() {
        // given
        CustomerModel customerBefore =  customerBuilder.setFirstName("Jan")
                .setLastName("Nowak")
                .setPesel("123")
                .build();
        CustomerModel customerAfter = customerBuilder.setFirstName("Adan")
                .setLastName("Kowalski")
                .setPesel("123")
                .build();
        Mockito.when(mockRepository.findByPesel(customerBefore.getPesel())).thenReturn(Arrays.asList(CustomerDBModel.from(customerBefore)));

        // when
        subject.updateCustomer(customerBefore, customerAfter);

        // then
        verify(mockRepository).save(CustomerDBModel.from(customerAfter));
    }

    @Test
    void updateCustomerThatExistMoreThanOnceTest() {
        // given
        CustomerModel customerBefore =  customerBuilder.setFirstName("Jan")
                .setLastName("Nowak")
                .setPesel("123")
                .build();
        CustomerModel customerAfter = customerBuilder.setFirstName("Adan")
                .setLastName("Kowalski")
                .setPesel("123")
                .build();
        Mockito.when(mockRepository.findByPesel(customerBefore.getPesel())).thenReturn(Arrays.asList(
                CustomerDBModel.from(customerBefore),
                CustomerDBModel.from(customerBefore)
        ));

        // when
        Throwable thrown = catchThrowable(()-> subject.updateCustomer(customerBefore, customerAfter));

        // then
        assertThat(thrown).isInstanceOf(ToManyCustomerException.class)
                .hasMessage(String.format("More than one customer found with pesel: %s", customerBefore.getPesel()));
    }
}