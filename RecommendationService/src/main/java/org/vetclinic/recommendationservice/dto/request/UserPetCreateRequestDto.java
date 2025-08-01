package org.vetclinic.recommendationservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.Set;

public record UserPetCreateRequestDto(

        @NotBlank
        @Size(max = 100)
        String name,

        @PastOrPresent
        LocalDate birthDate,

        @NotBlank
        @Size(max = 50)
        String speciesName,

        @Size(max = 100)
        String breedName,

        Set<@NotBlank @Size(max = 100) String> allergyNames
) {
}
