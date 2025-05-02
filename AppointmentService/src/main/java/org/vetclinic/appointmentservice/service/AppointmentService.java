package org.vetclinic.appointmentservice.service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.vetclinic.appointmentservice.dto.AppointmentMapper;
import org.vetclinic.appointmentservice.dto.AppointmentRequestDto;
import org.vetclinic.appointmentservice.dto.AppointmentResponseDto;
import org.vetclinic.appointmentservice.model.Appointment;
import org.vetclinic.appointmentservice.model.Doctor;
import org.vetclinic.appointmentservice.model.DoctorAvailability;
import org.vetclinic.appointmentservice.model.TimeSlot;
import org.vetclinic.appointmentservice.repository.AppointmentRepository;
import org.vetclinic.appointmentservice.repository.DoctorAvailabilityRepository;
import org.vetclinic.appointmentservice.repository.DoctorRepository;
import org.vetclinic.appointmentservice.repository.TimeSlotsRepository;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@Transactional
public class AppointmentService {
    private final AppointmentRepository appointmentRepository;
    private final DoctorAvailabilityRepository doctorAvailabilityRepository;
    private final TimeSlotsRepository timeSlotsRepository;
    private final AppointmentMapper appointmentMapper;
    private final DoctorRepository doctorRepository;

    public List<TimeSlot> getAllSlots() {
        return (List<TimeSlot>) timeSlotsRepository.findAll();
    }

    public TimeSlot getTimeSlotById(long id) {
        return timeSlotsRepository.findById(id).orElse(null);
    }

    public List<Long> getAvailableSlots() {
        List<TimeSlot> allSlots = (List<TimeSlot>) timeSlotsRepository.findAll();
        List<Long> availableSlotsId = new ArrayList<Long>();

        for (TimeSlot timeSlot : allSlots) {
            for (DoctorAvailability doctorAvailability : doctorAvailabilityRepository.findAll()) {
                if (doctorAvailability.isAvailable()){
                    availableSlotsId.add(timeSlot.getId());
                    break;
                }
            }
        }

        return availableSlotsId;
    }

    public AppointmentResponseDto createAppointment(AppointmentRequestDto appointmentRequestDto) {
        Appointment appointment = appointmentMapper.toEntity(appointmentRequestDto);

        List<Long> availableSlots = getAvailableSlots();
        // appointment.getSlot() == null
        // надо убрать автоматический mapper
        if (availableSlots.contains(appointment.getSlot().getId())) {
            appointment.setSlot(getTimeSlotById(appointmentRequestDto.requiredSlotId()));
            for (DoctorAvailability doctorAvailability : doctorAvailabilityRepository.findAll()) {
                if (doctorAvailability.getSlot().getId().equals(appointment.getSlot().getId()) &&
                        doctorAvailability.isAvailable()) {
                    doctorAvailability.setAvailable(false);
                    appointment.setDoctorName(doctorAvailability.getDoctor().getName());
                }
            }

            Appointment temp = appointmentRepository.save(appointment);
            return appointmentMapper.toDto(temp);
        }else{
            availableSlots.remove(appointment.getSlot().getId());
            throw new RuntimeException("Slot not available yet");
        }
    }

    public List<Appointment> getAllAppointments() {
        return (List<Appointment>) appointmentRepository.findAll();
    }
}
