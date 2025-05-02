package org.vetclinic.appointmentservice.repository;

import org.springframework.data.repository.CrudRepository;
import org.vetclinic.appointmentservice.model.TimeSlot;

public interface TimeSlotsRepository extends CrudRepository<TimeSlot, Long> {
}
