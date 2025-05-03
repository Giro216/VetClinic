package org.vetclinic.recommendationservice.service;

import org.vetclinic.recommendationservice.dto.request.UserPetCreateRequestDto;
import org.vetclinic.recommendationservice.dto.response.UserPetResponseDto;

public interface UserPetService {

    UserPetResponseDto createPetForUser(Long userId, UserPetCreateRequestDto dto);

}
