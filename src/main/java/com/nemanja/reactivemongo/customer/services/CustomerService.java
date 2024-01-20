package com.nemanja.reactivemongo.customer.services;

import com.nemanja.reactivemongo.customer.model.CustomerDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CustomerService {

    Mono<CustomerDto> findFirstByCustomerName(String customerName);
    Flux<CustomerDto> findByCustomerName(String customerName);
    Flux<CustomerDto> listCustomers();
    Mono<CustomerDto> saveCustomer(Mono<CustomerDto> customerDto);
    Mono<CustomerDto> saveCustomer(CustomerDto customerDTO);
    Mono<CustomerDto> getById(String customerId);
    Mono<CustomerDto> updateCustomer(String customerId, CustomerDto customerDTO);
    Mono<CustomerDto> patchCustomer(String customerId, CustomerDto customerDTO);
    Mono<Void> deleteCustomerById(String customerId);
}
