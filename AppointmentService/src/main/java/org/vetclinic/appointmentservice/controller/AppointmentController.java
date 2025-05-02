package org.vetclinic.appointmentservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.vetclinic.appointmentservice.repository.AppointmentRepository;
import org.vetclinic.appointmentservice.service.AppointmentService;
import org.vetclinic.appointmentservice.dto.*;
import org.vetclinic.appointmentservice.model.Appointment;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/appointments")
public class AppointmentController {

    private final AppointmentService service;
    private final AppointmentRepository appointmentRepository;

    @GetMapping("/")
    public ResponseEntity<List<Appointment>> getAllAppointments() {
        return ResponseEntity.status(HttpStatus.OK).body(service.getAllAppointments());
    }

    @PostMapping("/")
    public ResponseEntity<AppointmentResponseDto> createAppointment(
            @RequestBody @Valid AppointmentRequestDto request) {
        try {
            AppointmentResponseDto saved = service.createAppointment(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @DeleteMapping("/{appointmentId}")
    public void deleteAppointment(@PathVariable @Valid Integer appointmentId) {
        appointmentRepository.deleteById(appointmentId);
    }
}

