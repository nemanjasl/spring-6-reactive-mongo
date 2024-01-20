package com.nemanja.reactivemongo.customer.web.fn;

import com.nemanja.reactivemongo.common.TestUtil;
import com.nemanja.reactivemongo.customer.domain.Customer;
import com.nemanja.reactivemongo.customer.model.CustomerDto;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.FluxExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest
@AutoConfigureWebTestClient
class CustomerEndpointTest {

    @Autowired
    WebTestClient webTestClient;

    @Test
    void testPatchIdNotFound() {
        webTestClient.patch()
                .uri(CustomerRouterConfig.CUSTOMER_PATH_ID, 999)
                .body(Mono.just(TestUtil.getTestCustomer()), CustomerDto.class)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void testPatchIdFound() {
        CustomerDto customerDto = getSavedTestCustomer();

        webTestClient.patch()
                .uri(CustomerRouterConfig.CUSTOMER_PATH_ID, customerDto.getId())
                .body(Mono.just(customerDto), CustomerDto.class)
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    void testDeleteNotFound() {
        webTestClient.delete()
                .uri(CustomerRouterConfig.CUSTOMER_PATH_ID, 999)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    @Order(999)
    void testDeleteCustomer() {
        CustomerDto customerDto = getSavedTestCustomer();

        webTestClient.delete()
                .uri(CustomerRouterConfig.CUSTOMER_PATH_ID, customerDto.getId())
                .exchange()
                .expectStatus()
                .isNoContent();
    }

    @Test
    @Order(4)
    void testUpdateCustomerBadRequest() {
        CustomerDto testCustomer = getSavedTestCustomer();
        testCustomer.setCustomerName("");

        webTestClient.put()
                .uri(CustomerRouterConfig.CUSTOMER_PATH_ID, testCustomer)
                .body(Mono.just(testCustomer), CustomerDto.class)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void testUpdateCustomerNotFound() {
        webTestClient.put()
                .uri(CustomerRouterConfig.CUSTOMER_PATH_ID, 999)
                .body(Mono.just(TestUtil.getTestCustomer()), CustomerDto.class)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    @Order(3)
    void testUpdateCustomer() {
        CustomerDto customerDto = getSavedTestCustomer();
        customerDto.setCustomerName("Milojko");

        webTestClient.put()
                .uri(CustomerRouterConfig.CUSTOMER_PATH_ID, customerDto.getId())
                //it is working even without this
                //.header(TestUtil.CONTENT_TYPE, TestUtil.APPLICATION_JSON)
                .body(Mono.just(customerDto), CustomerDto.class)
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    void testCreateCustomerBadData() {
        Customer testCustomer = TestUtil.getTestCustomer();
        testCustomer.setCustomerName("");

        webTestClient.post().uri(CustomerRouterConfig.CUSTOMER_PATH)
                //it is working even without this
                //.header(TestUtil.CONTENT_TYPE, TestUtil.APPLICATION_JSON)
                .body(Mono.just(testCustomer), CustomerDto.class)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void testCreateCustomer() {
        CustomerDto testDto = getSavedTestCustomer();

        webTestClient.post().uri(CustomerRouterConfig.CUSTOMER_PATH)
                //it is working even without this
                //.header(TestUtil.CONTENT_TYPE, TestUtil.APPLICATION_JSON)
                .body(Mono.just(testDto), CustomerDto.class)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().exists("location");
    }

    @Test
    void testGetByIdNotFound() {
        webTestClient.get().uri(CustomerRouterConfig.CUSTOMER_PATH_ID, 999)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    @Order(1)
    void testGetById() {
        CustomerDto customerDto = getSavedTestCustomer();

        webTestClient.get().uri(CustomerRouterConfig.CUSTOMER_PATH_ID, customerDto.getId())
                .exchange()
                .expectStatus().isOk()
                .expectHeader().valueEquals(TestUtil.CONTENT_TYPE, TestUtil.APPLICATION_JSON)
                .expectBody(CustomerDto.class);
    }

    @Test
    @Order(2)
    void testListCustomersByName() {
        final String CUSTOMER_NAME = "Jovan";
        CustomerDto testDto = getSavedTestCustomer();
        testDto.setCustomerName(CUSTOMER_NAME);

        //create test data
        webTestClient.post().uri(CustomerRouterConfig.CUSTOMER_PATH)
                .header(TestUtil.CONTENT_TYPE, TestUtil.APPLICATION_JSON)
                .body(Mono.just(testDto), CustomerDto.class)
                .exchange();

        webTestClient.get().uri(UriComponentsBuilder
                        .fromPath(CustomerRouterConfig.CUSTOMER_PATH)
                        .queryParam("customerName", CUSTOMER_NAME).build().toUri())
                .exchange()
                .expectStatus().isOk()
                .expectHeader().valueEquals(TestUtil.CONTENT_TYPE, TestUtil.APPLICATION_JSON)
                .expectBody().jsonPath("$.size()").value(equalTo(1));
    }

    @Test
    @Order(2)
    void testListCustomers() {
        webTestClient.get().uri(CustomerRouterConfig.CUSTOMER_PATH)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().valueEquals(TestUtil.CONTENT_TYPE, TestUtil.APPLICATION_JSON)
                .expectBody().jsonPath("$.size()").value(greaterThan(1));
    }

    public CustomerDto getSavedTestCustomer(){
        FluxExchangeResult<CustomerDto> customerDtoFluxExchangeResult = webTestClient.post().uri(CustomerRouterConfig.CUSTOMER_PATH)
                .header(TestUtil.CONTENT_TYPE, TestUtil.APPLICATION_JSON)
                .body(Mono.just(TestUtil.getTestCustomer()), CustomerDto.class)
                .exchange()
                .returnResult(CustomerDto.class);

        List<String> location = customerDtoFluxExchangeResult.getResponseHeaders().get("Location");

        return webTestClient.get().uri(CustomerRouterConfig.CUSTOMER_PATH)
                .exchange().returnResult(CustomerDto.class).getResponseBody().blockFirst();
    }
}