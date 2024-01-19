package com.nemanja.reactivemongo.beer.services;

import com.nemanja.reactivemongo.beer.model.BeerDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface BeerService {

    Mono<BeerDto> findFirstByBeerName(String beerName);
    Flux<BeerDto> findByBeerStyle(String beerStyle);
    Flux<BeerDto> listBeers();
    Mono<BeerDto> saveBeer(Mono<BeerDto> beerDto);
    Mono<BeerDto> saveBeer(BeerDto beerDTO);
    Mono<BeerDto> getById(String beerId);
    Mono<BeerDto> updateBeer(String beerId, BeerDto beerDTO);
    Mono<BeerDto> patchBeer(String beerId, BeerDto beerDTO);
    Mono<Void> deleteBeerById(String beerId);
}
