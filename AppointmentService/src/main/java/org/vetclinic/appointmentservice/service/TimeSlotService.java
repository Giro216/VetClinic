package org.vetclinic.appointmentservice.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.vetclinic.appointmentservice.model.DoctorAvailability;
import org.vetclinic.appointmentservice.model.TimeSlot;
import org.vetclinic.appointmentservice.repository.DoctorAvailabilityRepository;
import org.vetclinic.appointmentservice.repository.TimeSlotsRepository;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@AllArgsConstructor
@Transactional
public class TimeSlotService {
    TimeSlotsRepository timeSlotsRepository;
    DoctorAvailabilityRepository doctorAvailabilityRepository;

    public List<TimeSlot> getAllSlots() {
        try {
            return (List<TimeSlot>) timeSlotsRepository.findAll();
        }catch (Exception e) {
            throw new EntityNotFoundException("Failed to fetch time slots: " + e.getMessage());
        }
    }

    public TimeSlot getTimeSlotById(Long id) {
        return timeSlotsRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("TimeSlot with ID " + id + " not found"));
    }

    public List<Long> getAvailableSlots() {
        List<DoctorAvailability> available = doctorAvailabilityRepository.findByAvailableTrue();
        return available.stream()
                .map(DoctorAvailability::getSlotId)
                .distinct()
                .collect(Collectors.toList());
    }

    public List<Long> getAvailableSlotsByDate(Date date) {
        // Преобразуем Date в LocalDate
        LocalDate targetDate = date.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();

        // Получаем все TimeSlot за указанную дату
        List<TimeSlot> slotsForDate = StreamSupport.stream(timeSlotsRepository.findAll().spliterator(), false)
                .filter(slot -> slot.getStartTime().toLocalDate().equals(targetDate))
                .toList();

        // Получаем ID доступных слотов
        return doctorAvailabilityRepository.findByAvailableTrue().stream()
                .map(DoctorAvailability::getSlotId)
                .filter(slotId -> slotsForDate.stream().anyMatch(slot -> slot.getId().equals(slotId)))
                .distinct()
                .collect(Collectors.toList());
    }

    public List<TimeSlot> getAvailableTimeSlotsByDate(Date date) {
        // Преобразуем Date в LocalDate
        LocalDate targetDate = date.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();

        // Получаем все TimeSlot за указанную дату
        List<TimeSlot> slotsForDate = StreamSupport.stream(timeSlotsRepository.findAll().spliterator(), false)
                .filter(slot -> slot.getStartTime().toLocalDate().equals(targetDate))
                .toList();

        // Фильтруем только те слоты, которые имеют доступные DoctorAvailability
        return slotsForDate.stream()
                .filter(slot -> doctorAvailabilityRepository.findByAvailableTrue().stream()
                        .anyMatch(da -> da.getSlotId().equals(slot.getId())))
                .collect(Collectors.toList());
    }
}
