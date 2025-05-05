package org.vetclinic.recommendationservice.dto.response;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

public record UserPetResponseDto(

        UUID petId,

        Long ownerId,

        String name,

        LocalDate birthDate,

        String speciesName,

        String breedName,

        Set<String> allergyNames
) {
}
