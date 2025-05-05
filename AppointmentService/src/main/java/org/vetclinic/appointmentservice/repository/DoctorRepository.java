package org.vetclinic.appointmentservice.repository;

import org.springframework.data.repository.CrudRepository;
import org.vetclinic.appointmentservice.model.Doctor;

public interface DoctorRepository extends CrudRepository<Doctor, Long> {
}
