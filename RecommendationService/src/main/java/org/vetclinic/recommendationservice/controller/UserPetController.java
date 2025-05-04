package org.vetclinic.recommendationservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.vetclinic.recommendationservice.dto.request.UserPetCreateRequestDto;
import org.vetclinic.recommendationservice.dto.request.UserPetUpdateRequestDto;
import org.vetclinic.recommendationservice.dto.response.UserPetResponseDto;
import org.vetclinic.recommendationservice.service.UserPetService;

import java.net.URI;
import java.util.List;
import java.util.UUID;

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

    @GetMapping
    public ResponseEntity<List<UserPetResponseDto>> getPets(@PathVariable Long userId) {
        List<UserPetResponseDto> pets = userPetService.getPetsForUser(userId);
        return ResponseEntity.ok(pets);
    }

    @GetMapping("/{petId}")
    public ResponseEntity<UserPetResponseDto> getPetById(@PathVariable Long userId, @PathVariable UUID petId) {
        UserPetResponseDto pet = userPetService.getPetByIdForUser(userId, petId);
        return ResponseEntity.ok(pet);
    }

    @PatchMapping("/{petId}")
    public ResponseEntity<UserPetResponseDto> updatePet(@PathVariable Long userId,
                                                        @PathVariable UUID petId,
                                                        @Valid @RequestBody UserPetUpdateRequestDto requestDto) {
        UserPetResponseDto updatedPet = userPetService.updatePetForUser(userId, petId, requestDto);
        return ResponseEntity.ok(updatedPet);
    }

}
