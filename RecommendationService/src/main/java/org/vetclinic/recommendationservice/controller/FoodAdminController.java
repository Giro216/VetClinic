package org.vetclinic.recommendationservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.vetclinic.recommendationservice.dto.request.FoodCreateRequestDto;
import org.vetclinic.recommendationservice.dto.request.FoodUpdateRequestDto;
import org.vetclinic.recommendationservice.dto.response.FoodRecommendationResponseDto;
import org.vetclinic.recommendationservice.service.FoodRecommendationService;

import java.net.URI;
import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping("/api/v1/admin/food")
@RequiredArgsConstructor
public class FoodAdminController {

    private final FoodRecommendationService foodRecommendationService;

    @PostMapping
    public ResponseEntity<FoodRecommendationResponseDto> createFood(@Valid @RequestBody FoodCreateRequestDto requestDto) {
        FoodRecommendationResponseDto createdFood = foodRecommendationService.createFood(requestDto);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{foodId}")
                .buildAndExpand(createdFood.foodId())
                .toUri();

        return ResponseEntity.created(location).body(createdFood);
    }

    @GetMapping
    public ResponseEntity<List<FoodRecommendationResponseDto>> getAllFood() {
        return ResponseEntity.ok(foodRecommendationService.getAllFood());
    }

    @GetMapping("/{foodId}")
    public ResponseEntity<FoodRecommendationResponseDto> getFoodById(@PathVariable UUID foodId) {
        return ResponseEntity.ok(foodRecommendationService.getFoodById(foodId));
    }

    @PatchMapping("/{foodId}")
    public ResponseEntity<FoodRecommendationResponseDto> updateFood(@PathVariable UUID foodId,
                                                                    @Valid @RequestBody FoodUpdateRequestDto requestDto) {
        FoodRecommendationResponseDto updatedFood = foodRecommendationService.updateFood(foodId, requestDto);
        return ResponseEntity.ok(updatedFood);
    }

    @DeleteMapping("/{foodId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteFood(@PathVariable UUID foodId) {
        foodRecommendationService.deleteFood(foodId);
    }

}
