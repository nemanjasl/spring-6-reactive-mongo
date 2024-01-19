package com.nemanja.reactivemongo.customer.mappers;

import com.nemanja.reactivemongo.customer.domain.Customer;
import com.nemanja.reactivemongo.customer.model.CustomerDto;
import org.mapstruct.Mapper;

@Mapper
public interface CustomerMapper {

    CustomerDto customerToCustomerDto(Customer customer);

    Customer customerDtoToCustomer(CustomerDto customerDTO);
}