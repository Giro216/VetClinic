package org.vetclinic.recommendationservice.service;

import org.vetclinic.recommendationservice.dto.request.FoodCreateRequestDto;
import org.vetclinic.recommendationservice.dto.request.FoodUpdateRequestDto;
import org.vetclinic.recommendationservice.dto.response.FoodRecommendationResponseDto;

import java.util.List;
import java.util.UUID;

public interface FoodRecommendationService {

    List<FoodRecommendationResponseDto> getFoodRecommendationsForPet(UUID petId);

    List<FoodRecommendationResponseDto> getAllFood();

    FoodRecommendationResponseDto getFoodById(UUID foodId);

    FoodRecommendationResponseDto createFood(FoodCreateRequestDto dto);

    FoodRecommendationResponseDto updateFood(UUID foodId, FoodUpdateRequestDto dto);

    void deleteFood(UUID foodId);

}
