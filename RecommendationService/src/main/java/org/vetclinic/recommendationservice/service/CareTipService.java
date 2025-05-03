package org.vetclinic.recommendationservice.service;

import org.vetclinic.recommendationservice.dto.response.CareTipResponseDto;

import java.util.List;
import java.util.UUID;

public interface CareTipService {

    List<CareTipResponseDto> getCareTipsForPet(UUID petId);

}
