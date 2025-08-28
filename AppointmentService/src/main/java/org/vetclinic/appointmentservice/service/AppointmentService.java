package org.vetclinic.appointmentservice.service;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.vetclinic.appointmentservice.dto.AppointmentRequestDto;
import org.vetclinic.appointmentservice.dto.AppointmentResponseDto;
import org.vetclinic.appointmentservice.dto.ManualAppointmentMapper;
import org.vetclinic.appointmentservice.model.Appointment;
import org.vetclinic.appointmentservice.model.DoctorAvailability;
import org.vetclinic.appointmentservice.model.TimeSlot;
import org.vetclinic.appointmentservice.repository.AppointmentRepository;
import org.vetclinic.appointmentservice.repository.DoctorAvailabilityRepository;
import org.vetclinic.appointmentservice.repository.TimeSlotsRepository;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@Transactional
public class AppointmentService {
    private final AppointmentRepository appointmentRepository;
    private final DoctorAvailabilityRepository doctorAvailabilityRepository;
    private final TimeSlotsRepository timeSlotsRepository;
    private final ManualAppointmentMapper appointmentMapper;
    private final TimeSlotService timeSlotService;
    private final StringRedisTemplate redisTemplate;

    public AppointmentResponseDto createAppointment(AppointmentRequestDto dto) {
        // Чистим кэш перед созданием записи
        redisTemplate.delete("available_slots");
        redisTemplate.delete("least_loaded_doctor:slot_" + dto.requiredSlotId());

        // Проверяем существование слота
        Optional<TimeSlot> currentTimeSlot = timeSlotsRepository.findById(dto.requiredSlotId());
        if (currentTimeSlot.isEmpty()) {
            throw new RuntimeException("Slot not found");
        }

        // Проверяем, что комбинация doctorId и slotId уникальна
        if (appointmentRepository.existsByDoctorIdAndSlotId(dto.doctorId(), dto.requiredSlotId())) {
            throw new RuntimeException("Doctor " + dto.doctorId() + " already booked for slot " + dto.requiredSlotId());
        }

        // Проверяем доступность слота (через кеш/БД)
        List<Long> availableSlots = timeSlotService.getCachedAvailableSlots();
        if (!availableSlots.contains(dto.requiredSlotId())) {
            throw new RuntimeException("Slot not available");
        }

        // Проверяем и обновляем DoctorAvailability
        Optional<DoctorAvailability> doctorAvailabilityOpt = doctorAvailabilityRepository
                .findAllBySlotId(dto.requiredSlotId())
                .stream()
                .filter(da -> da.getDoctor().getId().equals(dto.doctorId()) && da.isAvailable())
                .findFirst();

        if (doctorAvailabilityOpt.isEmpty()) {
            throw new RuntimeException("Doctor not available at slot " + currentTimeSlot.get().getStartTime());
        }

        DoctorAvailability doctorAvailability = doctorAvailabilityOpt.get();
        doctorAvailability.setAvailable(false);
        doctorAvailabilityRepository.save(doctorAvailability);

        // Создаём и сохраняем запись
        Appointment currentAppointment = appointmentRepository.save(
                appointmentMapper.toEntity(dto, currentTimeSlot.get())
        );
        return appointmentMapper.toDto(currentAppointment);
    }

    public List<Appointment> getAllAppointments() {
        return appointmentRepository.findAll();
    }

    public void deleteById(@Valid Long appointmentId) {
        // Чистим кэш
        redisTemplate.delete("available_slots");

        Optional<Appointment> currentAppointment = appointmentRepository.findById(appointmentId);
        currentAppointment.ifPresent(appointment ->
                redisTemplate.delete("least_loaded_doctor:slot_" + appointment.getSlot().getId())
        );

        if (currentAppointment.isEmpty()) {
            throw new RuntimeException("Appointment not found");
        }

        for (DoctorAvailability doctorAvailability : currentAppointment.get().getSlot().getAvailabilities()) {
            if (doctorAvailability.getDoctor().getId().equals(currentAppointment.get().getDoctorId())) {
                doctorAvailability.setAvailable(true);
                doctorAvailabilityRepository.save(doctorAvailability);
            }
        }

        appointmentRepository.deleteById(appointmentId);
    }
}
