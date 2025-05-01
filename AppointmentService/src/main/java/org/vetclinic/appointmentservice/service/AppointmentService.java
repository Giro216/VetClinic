package org.vetclinic.appointmentservice.service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.vetclinic.appointmentservice.model.Appointment;
import org.vetclinic.appointmentservice.repository.AppointmentRepository;

@Service
@AllArgsConstructor
@Transactional
public class AppointmentService {
    private final AppointmentRepository appointmentRepository;

    public Appointment createBooking(Appointment appointment) {
        return appointmentRepository.save(appointment);
    }
}
