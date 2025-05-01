package org.vetclinic.appointmentservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.vetclinic.appointmentservice.dto.AppointmentMapper;
import org.vetclinic.appointmentservice.service.AppointmentService;
import org.vetclinic.appointmentservice.dto.*;
import org.vetclinic.appointmentservice.model.Appointment;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/appointments")
public class AppointmentController {

    private final AppointmentService service;

    private final AppointmentMapper mapper;

    @PostMapping
    public ResponseEntity<AppointmentResponseDto> createAppointment(
            @RequestBody @Valid AppointmentRequestDto request) {
        Appointment saved = service.createBooking(mapper.toEntity(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toDto(saved));
    }
}

