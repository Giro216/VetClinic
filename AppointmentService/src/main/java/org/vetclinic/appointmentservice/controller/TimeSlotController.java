package org.vetclinic.appointmentservice.controller;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.vetclinic.appointmentservice.model.TimeSlot;
import org.vetclinic.appointmentservice.service.TimeSlotService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
    public ResponseEntity<List<Long>> getAvailableSlotsId(){
        List<Long> timeSlots = timeSlotService.getCachedAvailableSlots();
        return new ResponseEntity<>(timeSlots, HttpStatus.OK);
    }

    @GetMapping("/available_slots")
    public ResponseEntity<List<TimeSlot>> getAvailableSlots(){
        List<Long> timeSlots = timeSlotService.getCachedAvailableSlots();
        List<TimeSlot> timeSlotList = new ArrayList<>();

        for (Long slotId : timeSlots) {
            timeSlotList.add(timeSlotService.getTimeSlotById(slotId));
        }

        return new ResponseEntity<>(timeSlotList, HttpStatus.OK);
    }

    @GetMapping("/available_slots_id_by_date")
    public ResponseEntity<List<Long>> getAvailableSlotsByDate(@RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date date) {
        List<Long> slotIds = timeSlotService.getAvailableSlotsByDate(date);
        if (slotIds.isEmpty()) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(slotIds, HttpStatus.OK);
    }

    @GetMapping("/available_slots_by_date")
    public ResponseEntity<List<TimeSlot>> getAvailableTimeSlotsByDate(@RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date date) {
        List<TimeSlot> slots = timeSlotService.getAvailableTimeSlotsByDate(date);
        if (slots.isEmpty()) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(slots, HttpStatus.OK);
    }

}
