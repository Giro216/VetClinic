package org.vetclinic.recommendationservice.dto.request;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.UUID;

public record ReminderCreationRequestDto(

        @NotBlank
        @Size(min = 1, max = 50)
        String type,

        @Size(max = 255)
        String description,

        @NotNull
        @FutureOrPresent
        LocalDate dueDate,

        @NotNull
        UUID petId
) {
}
