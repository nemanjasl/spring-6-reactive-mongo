package com.nemanja.reactivemongo.bootstrap;

import com.nemanja.reactivemongo.beer.domain.Beer;
import com.nemanja.reactivemongo.beer.repositories.BeerRepository;
import com.nemanja.reactivemongo.customer.domain.Customer;
import com.nemanja.reactivemongo.customer.repositories.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;

@Component
@RequiredArgsConstructor
public class BootstrapData implements CommandLineRunner {

    private final BeerRepository beerRepository;
    private final CustomerRepository customerRepository;

    @Override
    public void run(String... args) throws Exception {
        beerRepository
                .deleteAll()
                .doOnSuccess(success -> loadBeerData())
                .subscribe();
        customerRepository
                .deleteAll()
                .doOnSuccess(success -> loadCustomerData())
                .subscribe();
    }

    private void loadBeerData() {
        beerRepository.count().subscribe(count -> {
            if (count != 0) {
                return;
            }

            Beer beer1 = Beer.builder()
                    .beerName("Galaxy Cat")
                    .beerStyle("Pale Ale")
                    .upc("12356")
                    .price(new BigDecimal("12.99"))
                    .quantityOnHand(122)
                    .createdDate(LocalDateTime.now())
                    .lastModifiedDate(LocalDateTime.now())
                    .build();

            Beer beer2 = Beer.builder()
                    .beerName("Crank")
                    .beerStyle("Pale Ale")
                    .upc("12356222")
                    .price(new BigDecimal("11.99"))
                    .quantityOnHand(392)
                    .createdDate(LocalDateTime.now())
                    .lastModifiedDate(LocalDateTime.now())
                    .build();

            Beer beer3 = Beer.builder()
                    .beerName("Sunshine City")
                    .beerStyle("IPA")
                    .upc("12356")
                    .price(new BigDecimal("13.99"))
                    .quantityOnHand(144)
                    .createdDate(LocalDateTime.now())
                    .lastModifiedDate(LocalDateTime.now())
                    .build();

            beerRepository.save(beer1).subscribe();
            beerRepository.save(beer2).subscribe();
            beerRepository.save(beer3).subscribe();
        });
    }

    private void loadCustomerData() {
        customerRepository.count().subscribe(count -> {
            if (count != 0) {
                return;
            }

            Customer c1 = Customer.builder().customerName("Pera").build();
            Customer c2 = Customer.builder().customerName("Mika").build();
            Customer c3 = Customer.builder().customerName("Zika").build();

            customerRepository.saveAll(Arrays.asList(c1, c2, c3)).subscribe();
        });
    }
}