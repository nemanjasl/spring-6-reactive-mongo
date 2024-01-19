package com.nemanja.reactivemongo.customer.services;

import com.nemanja.reactivemongo.customer.model.CustomerDto;
import reactor.core.publisher.Mono;

public interface CustomerService {
    Mono<CustomerDto> saveCustomer(Mono<CustomerDto> customerDto);

    Mono<CustomerDto> getCustomerById(String customerId);
}
