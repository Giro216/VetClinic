package org.vetclinic.recommendationservice.dto.mapper;

import org.mapstruct.*;
import org.vetclinic.recommendationservice.dto.request.FoodCreateRequestDto;
import org.vetclinic.recommendationservice.dto.request.FoodUpdateRequestDto;
import org.vetclinic.recommendationservice.dto.response.FoodRecommendationResponseDto;
import org.vetclinic.recommendationservice.model.Breed;
import org.vetclinic.recommendationservice.model.Food;
import org.vetclinic.recommendationservice.model.Ingredient;
import org.vetclinic.recommendationservice.model.Species;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE,
        imports = {Set.class, Collectors.class, Collections.class, Species.class, Breed.class, Ingredient.class})
public interface FoodMapper {

    FoodRecommendationResponseDto toFoodRecommendationResponseDto(Food food);

    @Mapping(target = "foodId", ignore = true)
    @Mapping(target = "targetSpecies", expression = "java(Collections.emptySet())")
    @Mapping(target = "targetBreeds", expression = "java(Collections.emptySet())")
    @Mapping(target = "ingredients", expression = "java(Collections.emptySet())")
    Food toFood(FoodCreateRequestDto dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "foodId", ignore = true)
    @Mapping(target = "targetSpecies", ignore = true)
    @Mapping(target = "targetBreeds", ignore = true)
    @Mapping(target = "ingredients", ignore = true)
    void partialUpdate(FoodUpdateRequestDto dto, @MappingTarget Food food);

}
