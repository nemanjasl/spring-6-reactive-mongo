package com.nemanja.reactivemongo.customer.web.fn;

import com.nemanja.reactivemongo.customer.model.CustomerDto;
import com.nemanja.reactivemongo.customer.services.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebInputException;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class CustomerHandler {

    private final CustomerService customerService;
    private final Validator validator;

    private void validate(CustomerDto customerDto) {
        Errors errors = new BeanPropertyBindingResult(customerDto, "customerDto");
        validator.validate(customerDto, errors);

        if (errors.hasErrors()) {
            throw new ServerWebInputException(errors.toString());
        }
    }

    public Mono<ServerResponse> deleteById(ServerRequest request) {
        return customerService.getById(request.pathVariable("customerId"))
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                .flatMap(customerDto -> customerService.deleteCustomerById(customerDto.getId()))
                .then(ServerResponse.noContent().build());
    }

    public Mono<ServerResponse> patchCustomerById(ServerRequest request) {
        return request.bodyToMono(CustomerDto.class)
                .doOnNext(this::validate)
                .flatMap(customerDto -> customerService
                        .patchCustomer(request.pathVariable("customerId"), customerDto))
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                .flatMap(savedDto -> ServerResponse.noContent().build());
    }

    public Mono<ServerResponse> updateCustomerById(ServerRequest request) {
        return request.bodyToMono(CustomerDto.class)
                .doOnNext(this::validate)
                .flatMap(customerDto -> customerService
                        .updateCustomer(request.pathVariable("customerId"), customerDto))
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                .flatMap(savedDto -> ServerResponse.noContent().build());
    }

    public Mono<ServerResponse> createNewCustomer(ServerRequest request) {
        return request.bodyToMono(CustomerDto.class)
                .doOnNext(this::validate)
                .flatMap(customerService::saveCustomer)
                .flatMap(savedDto -> ServerResponse
                        .created(UriComponentsBuilder
                                .fromPath(CustomerRouterConfig.CUSTOMER_PATH_ID)
                                .build(savedDto.getId()))
                        .build());
    }

    public Mono<ServerResponse> getCustomerById(ServerRequest request) {
        return ServerResponse.ok()
                .body(customerService.getById(request.pathVariable("customerId"))
                                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND))),
                        CustomerDto.class);
    }

    public Mono<ServerResponse> listCustomers(ServerRequest request) {
        Flux<CustomerDto> flux;

        if (request.queryParam("customerName").isPresent()) {
            flux = customerService.findByCustomerName(request.queryParam("customerName").get());
        } else {
            flux = customerService.listCustomers();
        }

        return ServerResponse.ok()
                .body(flux, CustomerDto.class);
    }
}
