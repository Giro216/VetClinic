package org.vetclinic.recommendationservice.dto.response;

import org.vetclinic.recommendationservice.model.ReminderStatus;

import java.time.LocalDate;
import java.util.UUID;

public record ReminderResponseDto(

        Long id,

        UUID petId,

        String petName,

        LocalDate dueDate,

        String type,

        String description,

        ReminderStatus status
) {
}
