package org.vetclinic.recommendationservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.vetclinic.recommendationservice.dto.request.CareTipCreateRequestDto;
import org.vetclinic.recommendationservice.dto.request.CareTipUpdateRequestDto;
import org.vetclinic.recommendationservice.dto.response.CareTipResponseDto;
import org.vetclinic.recommendationservice.service.CareTipService;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/care-tips")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3001", allowCredentials = "true")
public class CareTipAdminController {

    private final CareTipService careTipService;

    @PostMapping
    public ResponseEntity<CareTipResponseDto> createCareTip(@Valid @RequestBody CareTipCreateRequestDto requestDto) {
        CareTipResponseDto createdTip = careTipService.createCareTip(requestDto);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{tipId}")
                .buildAndExpand(createdTip.tipId())
                .toUri();

        return ResponseEntity.created(location).body(createdTip);
    }

    @GetMapping
    public ResponseEntity<List<CareTipResponseDto>> getAllCareTips() {
        return ResponseEntity.ok(careTipService.getAllCareTips());
    }

    @GetMapping("/{tipId}")
    public ResponseEntity<CareTipResponseDto> getCareTipById(@PathVariable UUID tipId) {
        return ResponseEntity.ok(careTipService.getCareTipById(tipId));
    }

    @PatchMapping("/{tipId}")
    public ResponseEntity<CareTipResponseDto> updateCareTip(@PathVariable UUID tipId,
                                                            @Valid @RequestBody CareTipUpdateRequestDto requestDto) {
        CareTipResponseDto updatedTip = careTipService.updateCareTip(tipId, requestDto);
        return ResponseEntity.ok(updatedTip);
    }

    @DeleteMapping("/{tipId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCareTip(@PathVariable UUID tipId) {
        careTipService.deleteCareTip(tipId);
    }

}
