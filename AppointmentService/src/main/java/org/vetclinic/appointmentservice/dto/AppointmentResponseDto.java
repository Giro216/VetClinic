package org.vetclinic.appointmentservice.dto;

import java.io.Serializable;
import java.time.OffsetDateTime;

public record AppointmentResponseDto(
     Long id,
     Long petId,
     String doctorName,
     OffsetDateTime datetime,
     String status,
     OffsetDateTime createdAt) implements Serializable {}

