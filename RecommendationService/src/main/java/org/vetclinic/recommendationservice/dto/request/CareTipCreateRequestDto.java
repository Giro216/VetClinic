package org.vetclinic.recommendationservice.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.Set;

public record CareTipCreateRequestDto(

        @NotBlank
        @Size(max = 255)
        String title,

        @NotBlank
        String content,

        @NotBlank
        @Size(max = 100)
        String category,

        @Min(0)
        Integer minAgeMonths,

        @Min(0)
        Integer maxAgeMonths,

        Set<@NotBlank @Size(max = 100) String> applicableSpeciesNames,

        Set<@NotBlank @Size(max = 100) String> applicableBreedNames,

        Set<@NotBlank @Size(max = 100) String> relevantAllergyNames
) {
}
