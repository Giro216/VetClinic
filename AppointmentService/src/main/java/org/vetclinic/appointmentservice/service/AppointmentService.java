package org.vetclinic.appointmentservice.service;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
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
    private final ManualAppointmentMapper appointmentMapper;
    private final TimeSlotService timeSlotService;


    public AppointmentResponseDto createAppointment(AppointmentRequestDto dto) {
        List<Long> availableSlots = timeSlotService.getAvailableSlots();

        Appointment currentAppointment;
        Optional<TimeSlot> currentTimeSlot = timeSlotsRepository.
                findById(dto.requiredSlotId());

        if (currentTimeSlot.isEmpty()) {
            throw new RuntimeException("slot not found");
        }

        if (availableSlots.contains(currentTimeSlot.get().getId())) {
            for (DoctorAvailability doctorAvailability : doctorAvailabilityRepository.findAllBySlotId(currentTimeSlot.get().getId())) {
                if (!doctorAvailability.getDoctor().getId().equals(dto.doctorId())) {
                    continue;
                }
                if (doctorAvailability.isAvailable()){
                    doctorAvailability.setAvailable(false);
                }else{
                    throw new RuntimeException("Doctor not available at slot " + currentTimeSlot.get().getStartTime());
                }
            }

            currentAppointment = appointmentRepository.save(appointmentMapper.
                    toEntity(dto, currentTimeSlot.get()));
            return appointmentMapper.toDto(currentAppointment);
        }else{
            availableSlots.remove(currentTimeSlot.get().getId());
            throw new RuntimeException("Slot not available yet");
        }
    }

    public List<Appointment> getAllAppointments() {
        return appointmentRepository.findAll();
    }

    public void deleteById(@Valid Long appointmentId) {
        Optional<Appointment> currentAppointment = appointmentRepository.findById(appointmentId);
        if (currentAppointment.isEmpty()) {
            throw new RuntimeException("Appointment not found");
        }

        for (DoctorAvailability doctorAvailability : currentAppointment.get().getSlot().getAvailabilities()) {
            if (doctorAvailability.getDoctor().getId().equals(currentAppointment.get().getDoctorId())){
                doctorAvailability.setAvailable(true);
            }
        }

        appointmentRepository.deleteById(appointmentId);
    }
}
