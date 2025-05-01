package org.vetclinic.appointmentservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.vetclinic.appointmentservice.model.Appointment;

public interface AppointmentRepository extends JpaRepository<Appointment, Integer> {
}
