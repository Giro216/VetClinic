package org.vetclinic.recommendationservice.dto.mapper;

import org.mapstruct.*;
import org.vetclinic.recommendationservice.dto.request.CareTipCreateRequestDto;
import org.vetclinic.recommendationservice.dto.request.CareTipUpdateRequestDto;
import org.vetclinic.recommendationservice.dto.response.CareTipResponseDto;
import org.vetclinic.recommendationservice.model.Allergy;
import org.vetclinic.recommendationservice.model.Breed;
import org.vetclinic.recommendationservice.model.CareTip;
import org.vetclinic.recommendationservice.model.Species;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE,
        imports = {Set.class, Collectors.class, Collections.class, Species.class, Breed.class, Allergy.class})
public interface CareTipMapper {

    CareTipResponseDto toCareTipResponseDto(CareTip careTip);

    @Mapping(target = "tipId", ignore = true)
    @Mapping(target = "applicableSpecies", expression = "java(Collections.emptySet())")
    @Mapping(target = "applicableBreeds", expression = "java(Collections.emptySet())")
    @Mapping(target = "relevantAllergies", expression = "java(Collections.emptySet())")
    CareTip toCareTip(CareTipCreateRequestDto dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "tipId", ignore = true)
    @Mapping(target = "applicableSpecies", ignore = true)
    @Mapping(target = "applicableBreeds", ignore = true)
    @Mapping(target = "relevantAllergies", ignore = true)
    void partialUpdate(CareTipUpdateRequestDto dto, @MappingTarget CareTip careTip);

}
