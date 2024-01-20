package com.nemanja.reactivemongo.customer.services;

import com.nemanja.reactivemongo.customer.mappers.CustomerMapper;
import com.nemanja.reactivemongo.customer.model.CustomerDto;
import com.nemanja.reactivemongo.customer.repositories.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerMapper customerMapper;
    private final CustomerRepository customerRepository;

    @Override
    public Mono<CustomerDto> findFirstByCustomerName(String customerName) {
        return customerRepository.findFirstByCustomerName(customerName)
                .map(customerMapper::customerToCustomerDto);
    }

    public Flux<CustomerDto> findByCustomerName(String customerName) {
        return customerRepository.findByCustomerName(customerName)
                .map(customerMapper::customerToCustomerDto);
    }

    @Override
    public Flux<CustomerDto> listCustomers() {
        return customerRepository.findAll()
                .map(customerMapper::customerToCustomerDto);
    }

    @Override
    public Mono<CustomerDto> saveCustomer(Mono<CustomerDto> customerDto) {
        return customerDto.map(customerMapper::customerDtoToCustomer)
                .flatMap(customerRepository::save)
                .map(customerMapper::customerToCustomerDto);
    }

    @Override
    public Mono<CustomerDto> saveCustomer(CustomerDto customerDto) {
        return customerRepository.save(customerMapper.customerDtoToCustomer(customerDto))
                .map(customerMapper::customerToCustomerDto);
    }

    @Override
    public Mono<CustomerDto> getById(String customerId) {
        return customerRepository.findById(customerId)
                .map(customerMapper::customerToCustomerDto);
    }

    @Override
    public Mono<CustomerDto> updateCustomer(String customerId, CustomerDto customerDto) {
        return customerRepository.findById(customerId)
                .map(founcedCustomer -> {
                    founcedCustomer.setCustomerName(customerDto.getCustomerName());

                    return founcedCustomer;
                })
                .flatMap(customerRepository::save)
                .map(customerMapper::customerToCustomerDto);
    }

    @Override
    public Mono<CustomerDto> patchCustomer(String customerId, CustomerDto customerDto) {
        return customerRepository.findById(customerId)
                .map(foundedCustomer -> {
                    if (StringUtils.hasText(customerDto.getCustomerName())) {
                        foundedCustomer.setCustomerName(customerDto.getCustomerName());
                    }

                    return foundedCustomer;
                })
                .flatMap(customerRepository::save)
                .map(customerMapper::customerToCustomerDto);
    }

    @Override
    public Mono<Void> deleteCustomerById(String customerId) {
        return customerRepository.deleteById(customerId);
    }
}
