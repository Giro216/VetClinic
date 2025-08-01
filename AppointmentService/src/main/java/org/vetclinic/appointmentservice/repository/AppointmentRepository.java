package org.vetclinic.appointmentservice.repository;

import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.vetclinic.appointmentservice.model.Appointment;

import java.util.Optional;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    Optional<Appointment> findById(@NotNull Long Id);

    boolean existsByDoctorIdAndSlotId(Long doctorId, Long slotId);
}
