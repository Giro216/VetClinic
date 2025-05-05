package org.vetclinic.appointmentservice.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.vetclinic.appointmentservice.model.Doctor;
import org.vetclinic.appointmentservice.model.DoctorAvailability;
import org.vetclinic.appointmentservice.model.TimeSlot;
import org.vetclinic.appointmentservice.repository.DoctorRepository;
import org.vetclinic.appointmentservice.repository.TimeSlotsRepository;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner loadData(
            DoctorRepository doctorRepo,
            TimeSlotsRepository slotRepo
    ) {
        return args -> {
            if (doctorRepo.count() > 0) {
                return;
            }

            // 1) создаём 3 врачей
            List<Doctor> doctors = List.of(
                    new Doctor(null, "Иванов"),
                    new Doctor(null, "Петров"),
                    new Doctor(null, "Сидоров")
            );
            doctors = (List<Doctor>) doctorRepo.saveAll(doctors);

            // 2) вычисляем даты текущего месяца (пон–пят)
            LocalDate today = LocalDate.now();
            LocalDate endDate = today.plusMonths(1);
            List<LocalDate> workDays = new ArrayList<>();

            LocalDate currentDate = today;
            while (currentDate.isBefore(endDate)) {
                DayOfWeek dayOfWeek = currentDate.getDayOfWeek();
                if (dayOfWeek != DayOfWeek.SATURDAY && dayOfWeek != DayOfWeek.SUNDAY) {
                    workDays.add(currentDate);
                }
                currentDate = currentDate.plusDays(1);
            }

            // часы, кроме обеда
            List<Integer> hours = List.of(8,9,10,11,12,14,15,16);

            // 3) для каждого дня и часа создаём TimeSlot + DoctorAvailability
            for (LocalDate date : workDays) {
                for (int h : hours) {
                    LocalDateTime start = date.atTime(h, 0);

                    TimeSlot slot = new TimeSlot();
                    slot.setStartTime(start);
                    slot = slotRepo.save(slot);

                    List<DoctorAvailability> availList = new ArrayList<>();
                    for (Doctor doc : doctors) {
                        DoctorAvailability av = new DoctorAvailability();
                        av.setSlotId(slot.getId());
                        av.setDoctor(doc);
                        av.setAvailable(true);
                        availList.add(av);
                    }

                    slot.setAvailabilities(availList);
                    slotRepo.save(slot);
                }
            }

            System.out.println("=== Инициализация данных завершена ===");
        };
    }
}