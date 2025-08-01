package org.vetclinic.recommendationservice.dto.response;

import org.vetclinic.recommendationservice.model.Food;

import java.util.UUID;

/**
 * DTO for {@link Food}
 */
public record FoodRecommendationResponseDto(

        UUID foodId,

        String name,

        String brand,

        String description
) {
}
