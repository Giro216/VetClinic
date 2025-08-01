package org.vetclinic.appointmentservice.dto;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.UUID;

public record AppointmentResponseDto(
     Long id,
     UUID petId,
     String doctorName,
     OffsetDateTime datetime,
     String status,
     OffsetDateTime createdAt) implements Serializable {}

