package org.vetclinic.recommendationservice.dto.response;

import java.util.UUID;

public record CareTipResponseDto(

        UUID tipId,

        String title,

        String content,

        String category
) {
}
