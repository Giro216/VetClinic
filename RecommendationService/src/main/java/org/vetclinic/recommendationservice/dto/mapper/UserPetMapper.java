package org.vetclinic.recommendationservice.dto.mapper;

import org.mapstruct.*;
import org.vetclinic.recommendationservice.dto.response.UserPetResponseDto;
import org.vetclinic.recommendationservice.model.Allergy;
import org.vetclinic.recommendationservice.model.Pet;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserPetMapper {

    @Mapping(target = "speciesName", source = "species.name")
    @Mapping(target = "breedName", source = "breed.name")
    @Mapping(target = "allergyNames", source = "allergies", qualifiedByName = "allergiesToNames")
    UserPetResponseDto toUserPetResponseDto(Pet pet);

    @Named("allergiesToNames")
    default Set<String> allergiesToNames(Set<Allergy> allergies) {
        if (allergies == null) {
            return Collections.emptySet();
        }
        return allergies.stream()
                .map(Allergy::getName)
                .collect(Collectors.toSet());
    }

}
