package org.vetclinic.recommendationservice.service;

import org.vetclinic.recommendationservice.dto.request.CareTipCreateRequestDto;
import org.vetclinic.recommendationservice.dto.request.CareTipUpdateRequestDto;
import org.vetclinic.recommendationservice.dto.response.CareTipResponseDto;

import java.util.List;
import java.util.UUID;

public interface CareTipService {

    List<CareTipResponseDto> getCareTipsForPet(UUID petId);

    List<CareTipResponseDto> getAllCareTips();

    CareTipResponseDto getCareTipById(UUID tipId);

    CareTipResponseDto createCareTip(CareTipCreateRequestDto dto);

    CareTipResponseDto updateCareTip(UUID tipId, CareTipUpdateRequestDto dto);

    void deleteCareTip(UUID tipId);

}
