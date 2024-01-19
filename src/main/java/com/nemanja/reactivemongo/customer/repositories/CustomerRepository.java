package com.nemanja.reactivemongo.customer.repositories;

import com.nemanja.reactivemongo.customer.domain.Customer;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface CustomerRepository extends ReactiveMongoRepository<Customer, String> {

}
