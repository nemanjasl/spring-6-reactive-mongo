package com.nemanja.reactivemongo.customer.services;

import com.nemanja.reactivemongo.customer.mappers.CustomerMapper;
import com.nemanja.reactivemongo.customer.model.CustomerDto;
import com.nemanja.reactivemongo.customer.repositories.CustomerRepository;
import com.nemanja.reactivemongo.common.TestUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Mono;

import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@SpringBootTest
class CustomerServiceImplTest {

    @Autowired
    CustomerService customerService;
    @Autowired
    CustomerRepository customerRepository;
    @Autowired
    CustomerMapper customerMapper;

    CustomerDto customerDto;

    @BeforeEach
    void setUp() {
        customerDto = customerMapper.customerToCustomerDto(TestUtil.getTestCustomer());
    }

    @Test
    void testFindFirstByCustomerName() {
        AtomicReference<CustomerDto> atomicDto = new AtomicReference<>();

        CustomerDto newCustomerDto = getSavedCustomerDto();

        Mono<CustomerDto> foundedDto = customerService.findFirstByCustomerName(newCustomerDto.getCustomerName());

        foundedDto.subscribe(dto -> {
            System.out.println(dto);
            atomicDto.set(dto);
        });

        await().until(() -> atomicDto.get() != null);

        CustomerDto dto = atomicDto.get();

        assertThat(dto).isNotNull();
        //this can fail because we are searching for FIRST customer
        //assertThat(dto.getId()).isEqualTo(newCustomerDto.getId());
        assertThat(dto.getCustomerName()).isEqualTo(newCustomerDto.getCustomerName());
    }

    @Test
    @DisplayName("Test Save Customer Using Subscriber")
    void saveCustomerUseSubscriber() {
        AtomicReference<CustomerDto> atomicDto = new AtomicReference<>();

        customerService.saveCustomer(Mono.just(customerDto))
            .subscribe(atomicDto::set);

        await().until(() -> atomicDto.get() != null);

        CustomerDto persistedDto = atomicDto.get();
        assertThat(persistedDto).isNotNull();
        assertThat(persistedDto.getId()).isNotNull();
    }

    @Test
    @DisplayName("Test Save Customer Using Block")
    void testSaveCustomerUseBlock() {
        CustomerDto savedDto = customerService.saveCustomer(Mono.just(customerDto)).block();
        assertThat(savedDto).isNotNull();
        assertThat(savedDto.getId()).isNotNull();
    }

    @Test
    @DisplayName("Test Update Customer Using Block")
    void testUpdateBlocking() {
        final String newName = "New Customer Name";  // use final so cannot mutate
        CustomerDto savedCustomerDto = getSavedCustomerDto();
        savedCustomerDto.setCustomerName(newName);

        CustomerDto updatedDto = customerService.saveCustomer(Mono.just(savedCustomerDto)).block();

        //verify exists in db
        assert updatedDto != null;
        CustomerDto fetchedDto = customerService.getById(updatedDto.getId()).block();
        assert fetchedDto != null;
        assertThat(fetchedDto.getCustomerName()).isEqualTo(newName);
    }

    @Test
    @DisplayName("Test Update Using Reactive Streams")
    void testUpdateStreaming() {
        final String newName = "New Customer Name";  // use final so cannot mutate

        AtomicReference<CustomerDto> atomicDto = new AtomicReference<>();

        customerService.saveCustomer(Mono.just(customerDto))
                .map(savedCustomerDto -> {
                    savedCustomerDto.setCustomerName(newName);
                    return savedCustomerDto;
                })
                .flatMap(customerService::saveCustomer) // save updated customer
                .flatMap(savedUpdatedDto -> customerService.getById(savedUpdatedDto.getId())) // get from db
                .subscribe(atomicDto::set);

        await().until(() -> atomicDto.get() != null);
        assertThat(atomicDto.get().getCustomerName()).isEqualTo(newName);
    }

    @Test
    void testDeleteCustomer() {
        CustomerDto customerToDelete = getSavedCustomerDto();

        customerService.deleteCustomerById(customerToDelete.getId()).block();

        Mono<CustomerDto> expectedEmptyCustomerMono = customerService.getById(customerToDelete.getId());

        CustomerDto emptyCustomer = expectedEmptyCustomerMono.block();

        assertThat(emptyCustomer).isNull();
    }

    public CustomerDto getSavedCustomerDto() {
        Mono<CustomerDto> unsavedCustomerDto = Mono.just(customerMapper.customerToCustomerDto(TestUtil.getTestCustomer()));

        AtomicReference<CustomerDto> atomicDto = new AtomicReference<>();

        customerService.saveCustomer(unsavedCustomerDto)
                .subscribe(atomicDto::set);

        await().until(() -> atomicDto.get() != null);

        return atomicDto.get();
    }
}