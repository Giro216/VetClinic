package org.vetclinic.appointmentservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.vetclinic.appointmentservice.model.DoctorAvailability;
import org.vetclinic.appointmentservice.model.TimeSlot;
import org.vetclinic.appointmentservice.repository.DoctorAvailabilityRepository;
import org.vetclinic.appointmentservice.repository.TimeSlotsRepository;

import java.time.Duration;
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

    private final TimeSlotsRepository timeSlotsRepository;
    private final DoctorAvailabilityRepository doctorAvailabilityRepository;
    private final ObjectMapper objectMapper;               // Jackson bean из автоконфигурации Spring Boot
    private final StringRedisTemplate redisTemplate;       // Автосоздаётся Spring Boot по spring.redis.*

    public List<TimeSlot> getAllSlots() {
        try {
            return (List<TimeSlot>) timeSlotsRepository.findAll();
        } catch (Exception e) {
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

    public List<Long> getCachedAvailableSlots() {
        final String cacheKey = "available_slots";

        // читаем из кеша
        String cachedJson = redisTemplate.opsForValue().get(cacheKey);
        if (cachedJson != null && !cachedJson.isBlank()) {
            try {
                return objectMapper.readValue(
                        cachedJson,
                        objectMapper.getTypeFactory().constructCollectionType(List.class, Long.class)
                );
            } catch (JsonProcessingException e) {
                throw new EntityNotFoundException("Failed to parse cached data: " + e.getMessage());
            }
        }

        // нет в кеше — берём из БД и кладём в Redis c TTL 10 секунд
        List<Long> availableSlots = getAvailableSlots();
        try {
            String serialized = objectMapper.writeValueAsString(availableSlots);
            redisTemplate.opsForValue().set(cacheKey, serialized, Duration.ofSeconds(10));
        } catch (JsonProcessingException e) {
            throw new EntityNotFoundException("Failed to serialize slots: " + e.getMessage());
        }
        return availableSlots;
    }

    public List<Long> getAvailableSlotsByDate(Date date) {
        LocalDate targetDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        List<TimeSlot> slotsForDate = StreamSupport.stream(timeSlotsRepository.findAll().spliterator(), false)
                .filter(slot -> slot.getStartTime().toLocalDate().equals(targetDate))
                .toList();

        return doctorAvailabilityRepository.findByAvailableTrue().stream()
                .map(DoctorAvailability::getSlotId)
                .filter(slotId -> slotsForDate.stream().anyMatch(slot -> slot.getId().equals(slotId)))
                .distinct()
                .collect(Collectors.toList());
    }

    public List<TimeSlot> getAvailableTimeSlotsByDate(Date date) {
        LocalDate targetDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        List<TimeSlot> slotsForDate = StreamSupport.stream(timeSlotsRepository.findAll().spliterator(), false)
                .filter(slot -> slot.getStartTime().toLocalDate().equals(targetDate))
                .toList();

        return slotsForDate.stream()
                .filter(slot -> doctorAvailabilityRepository.findByAvailableTrue().stream()
                        .anyMatch(da -> da.getSlotId().equals(slot.getId())))
                .collect(Collectors.toList());
    }
}
