package org.vetclinic.recommendationservice.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.Set;

public record FoodCreateRequestDto(

        @NotBlank
        @Size(max = 150)
        String name,

        @NotBlank
        @Size(max = 100)
        String brand,

        @Size(max = 500)
        String description,

        @Min(0)
        Integer minAgeMonths,

        @Min(0)
        Integer maxAgeMonths,

        Set<@NotBlank @Size(max = 100) String> targetSpeciesNames,

        Set<@NotBlank @Size(max = 100) String> targetBreedNames,

        Set<@NotBlank @Size(max = 100) String> ingredientNames
) {
}
