package org.vetclinic.appointmentservice.service;

import org.springframework.stereotype.Service;
import org.vetclinic.appointmentservice.model.DoctorAvailability;
import org.vetclinic.appointmentservice.model.TimeSlot;
import org.vetclinic.appointmentservice.repository.DoctorAvailabilityRepository;
import org.vetclinic.appointmentservice.repository.TimeSlotsRepository;
import java.util.ArrayList;
import java.util.List;

@Service
public class TimeSlotService {
    TimeSlotsRepository timeSlotsRepository;
    DoctorAvailabilityRepository doctorAvailabilityRepository;

    public List<TimeSlot> getAllSlots() {
        return (List<TimeSlot>) timeSlotsRepository.findAll();
    }

    public TimeSlot getTimeSlotById(long id) {
        return timeSlotsRepository.findById(id).orElse(null);
    }

    public List<Long> getAvailableSlots() {
        List<TimeSlot> allSlots = (List<TimeSlot>) timeSlotsRepository.findAll();
        List<Long> availableSlotsId = new ArrayList<>();

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
}
