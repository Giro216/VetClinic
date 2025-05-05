package org.vetclinic.appointmentservice.dto;

import jakarta.validation.constraints.NotNull;
import java.io.Serializable;

public record AppointmentRequestDto(
    Integer petId,

    @NotNull
    Long doctorId,

    @NotNull
    Long requiredSlotId,

    String reason) implements Serializable{}

