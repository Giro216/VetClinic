package org.vetclinic.recommendationservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.vetclinic.recommendationservice.dto.request.UserPetCreateRequestDto;
import org.vetclinic.recommendationservice.dto.response.UserPetResponseDto;
import org.vetclinic.recommendationservice.service.UserPetService;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/users/{userId}/pets")
@RequiredArgsConstructor
public class UserPetController {

    private final UserPetService userPetService;

    @PostMapping
    public ResponseEntity<UserPetResponseDto> createPet(@PathVariable Long userId,
                                                        @Valid @RequestBody UserPetCreateRequestDto requestDto) {
        UserPetResponseDto createdPet = userPetService.createPetForUser(userId, requestDto);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{petId}")
                .buildAndExpand(createdPet.petId())
                .toUri();

        return ResponseEntity.created(location).body(createdPet);
    }

}
