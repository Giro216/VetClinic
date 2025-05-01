package org.vetclinic.appointmentservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.time.OffsetDateTime;

public record AppointmentRequestDto(
    Integer petId,

    @NotBlank
    Integer doctorId,

    @NotBlank
    OffsetDateTime datetime,

    String reason) implements Serializable{}

