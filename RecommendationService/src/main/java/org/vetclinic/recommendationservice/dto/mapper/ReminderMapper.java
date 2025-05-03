package org.vetclinic.recommendationservice.dto.mapper;

import org.mapstruct.*;
import org.vetclinic.recommendationservice.dto.request.ReminderCreationRequestDto;
import org.vetclinic.recommendationservice.dto.request.ReminderUpdateRequestDto;
import org.vetclinic.recommendationservice.dto.response.ReminderResponseDto;
import org.vetclinic.recommendationservice.model.Pet;
import org.vetclinic.recommendationservice.model.Reminder;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ReminderMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "pet", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void partialUpdate(ReminderUpdateRequestDto reminderUpdateRequestDto, @MappingTarget Reminder reminder);

    @Mapping(source = "pet.petId", target = "petId")
    @Mapping(source = "pet.name", target = "petName")
    ReminderResponseDto toReminderResponseDto(Reminder reminder);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(source = "pet", target = "pet")
    Reminder toReminder(ReminderCreationRequestDto reminderCreationRequestDto, Pet pet);

}
