package org.vetclinic.recommendationservice.service;

import org.vetclinic.recommendationservice.dto.response.FoodRecommendationResponseDto;

import java.util.List;
import java.util.UUID;

public interface FoodRecommendationService {

    List<FoodRecommendationResponseDto> getFoodRecommendationsForPet(UUID petId);

}
