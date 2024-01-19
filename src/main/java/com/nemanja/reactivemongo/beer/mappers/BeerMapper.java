package com.nemanja.reactivemongo.beer.mappers;

import com.nemanja.reactivemongo.beer.domain.Beer;
import com.nemanja.reactivemongo.beer.model.BeerDto;
import org.mapstruct.Mapper;

@Mapper
public interface BeerMapper {

    BeerDto beerToBeerDto(Beer beer);

    Beer beerDtoToBeer(BeerDto beerDTO);
}