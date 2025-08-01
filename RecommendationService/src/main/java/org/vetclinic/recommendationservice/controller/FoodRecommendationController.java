package org.vetclinic.recommendationservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.vetclinic.recommendationservice.dto.response.FoodRecommendationResponseDto;
import org.vetclinic.recommendationservice.service.FoodRecommendationService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/pets/{petId}/food-recommendations")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3001", allowCredentials = "true")
public class FoodRecommendationController {

    private final FoodRecommendationService foodRecommendationService;

    @GetMapping
    public ResponseEntity<List<FoodRecommendationResponseDto>> getFoodRecommendations(@PathVariable UUID petId) {
        List<FoodRecommendationResponseDto> recommendations = foodRecommendationService.getFoodRecommendationsForPet(petId);
        return ResponseEntity.ok(recommendations);
    }

}
