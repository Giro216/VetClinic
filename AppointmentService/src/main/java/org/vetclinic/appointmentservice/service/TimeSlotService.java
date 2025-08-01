package org.vetclinic.appointmentservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.vetclinic.appointmentservice.model.DoctorAvailability;
import org.vetclinic.appointmentservice.model.TimeSlot;
import org.vetclinic.appointmentservice.repository.DoctorAvailabilityRepository;
import org.vetclinic.appointmentservice.repository.TimeSlotsRepository;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
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
    private final ObjectMapper objectMapper; // Для сериализации/десериализации JSON

    public JedisPool jedisPool() {
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(10); // Максимум 10 соединений
        poolConfig.setMaxIdle(5);   // Максимум 5 неактивных соединений
        return new JedisPool(poolConfig, "localhost", 6379); // Параметры подключения к Redis
    }

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

    public List<Long> getCachedAvailableSlots() {
        String cacheKey = "available_slots";

        try(Jedis jedis = jedisPool().getResource()){
            // Проверяем, есть ли данные в кеше
            String cashedData = jedis.get(cacheKey);
            if(cashedData != null){
                return objectMapper.readValue(cashedData, objectMapper.getTypeFactory().constructCollectionType(List.class, Long.class));
            }

            // Если нет
            List<Long> availableSlots = getAvailableSlots();
            String serializedData = objectMapper.writeValueAsString(availableSlots);
            jedis.setex(cacheKey, 10, serializedData);

            return availableSlots;
        }catch (JsonProcessingException e){
            throw new EntityNotFoundException("Failed to fetch available slots: " + e.getMessage());
        }
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
