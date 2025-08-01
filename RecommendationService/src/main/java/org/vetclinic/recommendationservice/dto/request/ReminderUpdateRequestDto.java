package org.vetclinic.recommendationservice.dto.request;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Size;
import org.vetclinic.recommendationservice.model.ReminderStatus;

import java.time.LocalDate;

public record ReminderUpdateRequestDto(

        @Size(min = 1, max = 50)
        String type,

        @Size(max = 255)
        String description,

        @FutureOrPresent
        LocalDate dueDate,

        ReminderStatus status
) {
}
