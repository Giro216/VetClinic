package org.vetclinic.appointmentservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.vetclinic.appointmentservice.service.AppointmentService;
import org.vetclinic.appointmentservice.dto.*;
import org.vetclinic.appointmentservice.model.Appointment;

import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/appointments")
@CrossOrigin(origins = "http://localhost:3001", allowCredentials = "true")
public class AppointmentController {

    private final AppointmentService appointmentService;

    @GetMapping("/")
    public ResponseEntity<List<Appointment>> getAllAppointments() {
        log.info("Запрос на получение всех записей");
        return ResponseEntity.status(HttpStatus.OK).body(appointmentService.getAllAppointments());
    }

    @PostMapping("/")
    public ResponseEntity<AppointmentResponseDto> createAppointment(
            @RequestBody @Valid AppointmentRequestDto request) {
        log.debug("Создание записи: {}", request);
        try {
            AppointmentResponseDto saved = appointmentService.createAppointment(request);
            log.info("Запись успешно создана с id={}", saved.id());
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (Exception e) {
            log.error("Ошибка при создании записи: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @DeleteMapping("/{appointmentId}")
    public void deleteAppointment(@PathVariable @Valid Long appointmentId) {
        log.warn("Удаление записи с id={}", appointmentId);
        try {
            appointmentService.deleteById(appointmentId);
            log.info("Запись удалена: {}", appointmentId);
        } catch (Exception e) {
            log.error("Ошибка при удалении записи {}: {}", appointmentId, e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }
}
