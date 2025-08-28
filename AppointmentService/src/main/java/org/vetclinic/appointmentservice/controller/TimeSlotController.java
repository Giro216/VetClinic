package org.vetclinic.appointmentservice.controller;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.vetclinic.appointmentservice.model.TimeSlot;
import org.vetclinic.appointmentservice.service.TimeSlotService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/appointments/time_slots")
@CrossOrigin(origins = "http://localhost:3001", allowCredentials = "true")
public class TimeSlotController {
    private final TimeSlotService timeSlotService;

    @GetMapping("/")
    public ResponseEntity<List<TimeSlot>> getAllTimeSlots() {
        List<TimeSlot> timeSlots;
        try {
            timeSlots = timeSlotService.getAllSlots();
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(timeSlots, HttpStatus.OK);
    }

    @GetMapping("/{slotId}")
    public ResponseEntity<TimeSlot> getTimeSlotById(@PathVariable @Valid Long slotId) {
        try {
            TimeSlot timeSlot = timeSlotService.getTimeSlotById(slotId);
            return new ResponseEntity<>(timeSlot, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/available_slots_id")
    public ResponseEntity<List<Long>> getAvailableSlotsId() {
        try {
            log.info("Запрос на получение ID доступных слотов");
            List<Long> timeSlots = timeSlotService.getCachedAvailableSlots();
            if (timeSlots == null) {
                log.warn("Список ID слотов пуст или не удалось получить данные из кэша");
                return new ResponseEntity<>(Collections.emptyList(), HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(timeSlots, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Ошибка при получении ID доступных слотов: {}", e.getMessage(), e);
            return new ResponseEntity<>(Collections.emptyList(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/available_slots")
    public ResponseEntity<List<TimeSlot>> getAvailableSlots() {
        try {
            log.info("Запрос на получение доступных слотов");
            List<Long> timeSlots = timeSlotService.getCachedAvailableSlots();
//          List<Long> timeSlots = timeSlotService.getAvailableSlots();
            if (timeSlots == null || timeSlots.isEmpty()) {
                log.warn("Список ID слотов пуст или не удалось получить данные из кэша");
                return new ResponseEntity<>(Collections.emptyList(), HttpStatus.NO_CONTENT);
            }

            List<TimeSlot> timeSlotList = new ArrayList<>();
            //TODO попробовать избавиться от Long на long
            for (Long slotId : timeSlots) {
                TimeSlot timeSlot = timeSlotService.getTimeSlotById(slotId);
                if (timeSlot != null) {
                    timeSlotList.add(timeSlot);
                } else {
                    log.warn("Слот с ID {} не найден", slotId);
                }
            }

            if (timeSlotList.isEmpty()) {
                log.warn("Не найдено ни одного доступного слота");
                return new ResponseEntity<>(Collections.emptyList(), HttpStatus.OK);
            }
            return new ResponseEntity<>(timeSlotList, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Ошибка при получении доступных слотов: {}", e.getMessage(), e);
            return new ResponseEntity<>(Collections.emptyList(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/available_slots_id_by_date")
    public ResponseEntity<List<Long>> getAvailableSlotsByDate(
            @RequestParam("date") @NotNull(message = "Дата не должна быть пустой")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date date) {
        try {
            log.info("Запрос на получение ID слотов по дате: {}", date);
            if (date == null) {
                log.warn("Передана пустая дата");
                return new ResponseEntity<>(Collections.emptyList(), HttpStatus.BAD_REQUEST);
            }
            List<Long> slotIds = timeSlotService.getAvailableSlotsByDate(date);
            if (slotIds.isEmpty()) {
                log.info("Слоты на дату {} не найдены", date);
                return new ResponseEntity<>(Collections.emptyList(), HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(slotIds, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Ошибка при получении ID слотов по дате {}: {}", date, e.getMessage(), e);
            return new ResponseEntity<>(Collections.emptyList(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/available_slots_by_date")
    public ResponseEntity<List<TimeSlot>> getAvailableTimeSlotsByDate(
            @RequestParam("date") @NotNull(message = "Дата не должна быть пустой")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date date) {
        try {
            log.info("Запрос на получение слотов по дате: {}", date);
            if (date == null) {
                log.warn("Передана пустая дата");
                return new ResponseEntity<>(Collections.emptyList(), HttpStatus.BAD_REQUEST);
            }
            List<TimeSlot> slots = timeSlotService.getAvailableTimeSlotsByDate(date);
            if (slots.isEmpty()) {
                log.info("Слоты на дату {} не найдены", date);
                return new ResponseEntity<>(Collections.emptyList(), HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(slots, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Ошибка при получении слотов по дате {}: {}", date, e.getMessage(), e);
            return new ResponseEntity<>(Collections.emptyList(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
