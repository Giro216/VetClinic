package org.vetclinic.recommendationservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.vetclinic.recommendationservice.dto.response.FoodRecommendationResponseDto;
import org.vetclinic.recommendationservice.service.FoodRecommendationService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/recommendation/api/v1/pets/{petId}/food-recommendations")
@RequiredArgsConstructor
public class FoodRecommendationController {

    private final FoodRecommendationService foodRecommendationService;

    @GetMapping
    public ResponseEntity<List<FoodRecommendationResponseDto>> getFoodRecommendations(@PathVariable UUID petId) {
        List<FoodRecommendationResponseDto> recommendations = foodRecommendationService.getFoodRecommendationsForPet(petId);
        return ResponseEntity.ok(recommendations);
    }

}
