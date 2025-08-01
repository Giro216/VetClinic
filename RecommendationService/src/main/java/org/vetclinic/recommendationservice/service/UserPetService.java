package org.vetclinic.recommendationservice.service;

import org.vetclinic.recommendationservice.dto.request.UserPetCreateRequestDto;
import org.vetclinic.recommendationservice.dto.request.UserPetUpdateRequestDto;
import org.vetclinic.recommendationservice.dto.response.UserPetResponseDto;

import java.util.List;
import java.util.UUID;

public interface UserPetService {

    UserPetResponseDto createPetForUser(Long userId, UserPetCreateRequestDto dto);

    List<UserPetResponseDto> getPetsForUser(Long userId);

    UserPetResponseDto getPetByIdForUser(Long userId, UUID petId);

    UserPetResponseDto updatePetForUser(Long userId, UUID petId, UserPetUpdateRequestDto dto);

}
