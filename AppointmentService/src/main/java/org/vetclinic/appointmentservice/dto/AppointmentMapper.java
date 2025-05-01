package org.vetclinic.appointmentservice.dto;

import org.vetclinic.appointmentservice.model.Appointment;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface AppointmentMapper {

    Appointment toEntity(AppointmentRequestDto appointmentRequestDto);

    AppointmentResponseDto toDto(Appointment entity);
}
