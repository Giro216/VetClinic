package org.vetclinic.appointmentservice.dto;

import java.io.Serializable;
import java.time.OffsetDateTime;

public record AppointmentResponseDto(
     Integer id,
     Integer petId,
     Integer doctorId,
     OffsetDateTime datetime,
     String status,
     OffsetDateTime createdAt) implements Serializable {}

