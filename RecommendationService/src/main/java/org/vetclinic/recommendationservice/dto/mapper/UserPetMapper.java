package org.vetclinic.recommendationservice.dto.mapper;

import org.mapstruct.*;
import org.vetclinic.recommendationservice.dto.request.UserPetUpdateRequestDto;
import org.vetclinic.recommendationservice.dto.response.UserPetResponseDto;
import org.vetclinic.recommendationservice.model.Allergy;
import org.vetclinic.recommendationservice.model.Pet;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE,
        imports = {Set.class, Collectors.class, Collections.class, Allergy.class})
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

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "petId", ignore = true)
    @Mapping(target = "ownerId", ignore = true)
    @Mapping(target = "species", ignore = true)
    @Mapping(target = "breed", ignore = true)
    @Mapping(target = "allergies", ignore = true)
    void partialUpdate(UserPetUpdateRequestDto dto, @MappingTarget Pet pet);

}
