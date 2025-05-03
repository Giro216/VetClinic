package org.vetclinic.recommendationservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.vetclinic.recommendationservice.dto.response.CareTipResponseDto;
import org.vetclinic.recommendationservice.service.CareTipService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/pets/{petId}/care-tips")
@RequiredArgsConstructor
public class CareTipController {

    private final CareTipService careTipService;

    @GetMapping
    public ResponseEntity<List<CareTipResponseDto>> getCareTips(@PathVariable UUID petId) {
        List<CareTipResponseDto> tips = careTipService.getCareTipsForPet(petId);
        return ResponseEntity.ok(tips);
    }

}
