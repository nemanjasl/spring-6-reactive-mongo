package com.nemanja.reactivemongo.customer.services;

import com.nemanja.reactivemongo.customer.mappers.CustomerMapper;
import com.nemanja.reactivemongo.customer.model.CustomerDto;
import com.nemanja.reactivemongo.customer.repositories.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerMapper customerMapper;
    private final CustomerRepository customerRepository;

    @Override
    public Mono<CustomerDto> saveCustomer(Mono<CustomerDto> customerDto) {
        return customerDto.map(customerMapper::customerDtoToCustomer)
                .flatMap(customerRepository::save)
                .map(customerMapper::customerToCustomerDto);
    }

    @Override
    public Mono<CustomerDto> getCustomerById(String customerId) {
        return null;
    }
}
