package com.nemanja.reactivemongo.common;

import com.nemanja.reactivemongo.beer.domain.Beer;
import com.nemanja.reactivemongo.customer.domain.Customer;

import java.math.BigDecimal;

public class TestUtil {

    public static final String CONTENT_TYPE = "Content-Type";
    public static final String APPLICATION_JSON = "application/json";

    public static Beer getTestBeer() {
        return Beer.builder()
                .beerName("Space Dust")
                .beerStyle("IPA")
                .price(BigDecimal.TEN)
                .quantityOnHand(12)
                .upc("123456789")
                .build();
    }

    public static Customer getTestCustomer() {
        return Customer.builder()
                .customerName("Stanislav")
                .build();
    }
}
