package org.vetclinic.appointmentservice.dto;

import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.UUID;

public record AppointmentRequestDto(
    UUID petId,

    @NotNull
    Long doctorId,

    @NotNull
    Long requiredSlotId,

    String reason) implements Serializable{}

