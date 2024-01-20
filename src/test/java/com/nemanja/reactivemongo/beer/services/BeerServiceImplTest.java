package com.nemanja.reactivemongo.beer.services;

import com.nemanja.reactivemongo.beer.domain.Beer;
import com.nemanja.reactivemongo.beer.mappers.BeerMapper;
import com.nemanja.reactivemongo.beer.mappers.BeerMapperImpl;
import com.nemanja.reactivemongo.beer.model.BeerDto;
import com.nemanja.reactivemongo.beer.repositories.BeerRepository;
import com.nemanja.reactivemongo.common.TestUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Mono;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;


@SpringBootTest
class BeerServiceImplTest {

    @Autowired
    BeerService beerService;
    @Autowired
    BeerRepository beerRepository;
    @Autowired
    BeerMapper beerMapper;

    BeerDto beerDto;

    @BeforeEach
    void setUp() {
        beerDto = beerMapper.beerToBeerDto(TestUtil.getTestBeer());
    }

    @Test
    void testFindByBeerStyle() {
        AtomicReference<BeerDto> atomicDto = new AtomicReference<>();

        BeerDto newBeerDto = getSavedBeerDto();

        beerService.findByBeerStyle(newBeerDto.getBeerStyle())
                .subscribe(dto -> {
                    System.out.println(dto);
                    atomicDto.set(dto);
                });

        await().until(() -> atomicDto.get() != null);

        BeerDto dto = atomicDto.get();
        assertThat(dto).isNotNull();
        assertThat(dto.getBeerStyle()).isEqualTo(dto.getBeerStyle());
    }

    @Test
    void testFindFirstByBeerName() {
        AtomicReference<BeerDto> atomicDto = new AtomicReference<>();

        BeerDto newBeerDto = getSavedBeerDto();

        beerService.findFirstByBeerName(newBeerDto.getBeerName())
            .subscribe(dto -> {
                System.out.println(dto);
                atomicDto.set(dto);
            });

        await().until(() -> atomicDto.get() != null);

        BeerDto dto = atomicDto.get();
        assertThat(dto).isNotNull();
        assertThat(dto.getBeerName()).isEqualTo(newBeerDto.getBeerName());
    }

    @Test
    @DisplayName("Test Save Beer Using Subscriber")
    void saveBeerUseSubscriber() {
        AtomicBoolean atomicBoolean = new AtomicBoolean(false);
        AtomicReference<BeerDto> atomicDto = new AtomicReference<>();

        Mono<BeerDto> savedMono = beerService.saveBeer(Mono.just(beerDto));

        savedMono.subscribe(savedDto -> {
            System.out.println(savedDto.getId());
            atomicBoolean.set(true);
            atomicDto.set(savedDto);
        });

        await().untilTrue(atomicBoolean);

        BeerDto persistedDto = atomicDto.get();
        assertThat(persistedDto).isNotNull();
        assertThat(persistedDto.getId()).isNotNull();
    }

    @Test
    @DisplayName("Test Save Beer Using Block")
    void testSaveBeerUseBlock() {
        BeerDto savedDto = beerService.saveBeer(
                Mono.just(beerMapper.beerToBeerDto(TestUtil.getTestBeer()))
        ).block();
        assertThat(savedDto).isNotNull();
        assertThat(savedDto.getId()).isNotNull();
    }

    @Test
    @DisplayName("Test Update Beer Using Block")
    void testUpdateBlocking() {
        final String newName = "New Beer Name";  // use final so cannot mutate
        BeerDto savedBeerDto = getSavedBeerDto();
        savedBeerDto.setBeerName(newName);

        BeerDto updatedDto = beerService.saveBeer(Mono.just(savedBeerDto)).block();

        //verify exists in dbblic CustomerDto getTestCustomerDto() {
        //        return customerMapper.customerToCustomerDto(TestUtil.getTestCustomer());
        //    }
        BeerDto fetchedDto = beerService.getById(updatedDto.getId()).block();
        assertThat(fetchedDto.getBeerName()).isEqualTo(newName);
    }

    @Test
    @DisplayName("Test Update Using Reactive Streams")
    void testUpdateStreaming() {
        final String newName = "New Beer Name";  // use final so cannot mutate

        AtomicReference<BeerDto> atomicDto = new AtomicReference<>();

        beerService.saveBeer(Mono.just(beerMapper.beerToBeerDto(TestUtil.getTestBeer())))
                .map(savedBeerDto -> {
                    savedBeerDto.setBeerName(newName);
                    return savedBeerDto;
                })
                .flatMap(beerService::saveBeer) // save updated beer
                .flatMap(savedUpdatedDto -> beerService.getById(savedUpdatedDto.getId())) // get from db
                .subscribe(dtoFromDb -> {
                    atomicDto.set(dtoFromDb);
                });

        await().until(() -> atomicDto.get() != null);
        assertThat(atomicDto.get().getBeerName()).isEqualTo(newName);
    }

    @Test
    void testDeleteBeer() {
        BeerDto beerToDelete = getSavedBeerDto();

        beerService.deleteBeerById(beerToDelete.getId()).block();

        Mono<BeerDto> expectedEmptyBeerMono = beerService.getById(beerToDelete.getId());

        BeerDto emptyBeer = expectedEmptyBeerMono.block();

        assertThat(emptyBeer).isNull();
    }

    public BeerDto getSavedBeerDto() {
        //return beerService.saveBeer(Mono.just(getTestBeerDto())).block();
        AtomicReference<BeerDto> atomicDto = new AtomicReference<>();

        beerService.saveBeer(Mono.just(beerMapper.beerToBeerDto(TestUtil.getTestBeer())))
                .subscribe(savedDto -> {
                    atomicDto.set(savedDto);
                });

        await().until(() -> atomicDto.get() != null);

        return atomicDto.get();
    }
}