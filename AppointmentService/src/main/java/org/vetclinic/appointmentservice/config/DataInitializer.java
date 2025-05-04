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
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner loadData(
            DoctorRepository doctorRepo,
            TimeSlotsRepository slotRepo
    ) {
        return args -> {
            if (doctorRepo.count() > 0) {
                // уже проинициализировано
                return;
            }

            // 1) создаём 3 врачей
            List<Doctor> doctors = List.of(
                    new Doctor(null, "Иванов"),
                    new Doctor(null, "Петров"),
                    new Doctor(null, "Сидоров")
            );
            doctors = (List<Doctor>) doctorRepo.saveAll(doctors);

            // 2) вычисляем даты текущей недели (пон–пят)
            LocalDate monday = LocalDate.now()
                    .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
            List<LocalDate> days = IntStream.range(0, 5)
                    .mapToObj(monday::plusDays)
                    .toList();

            // часы, кроме обеда
            List<Integer> hours = List.of(8,9,10,11,12, 14,15,16);

            // 3) для каждого дня и часа создаём TimeSlot + DoctorAvailability
            for (LocalDate date : days) {
                for (int h : hours) {
                    LocalDateTime start = date.atTime(h, 0);

                    // Создаём TimeSlot без availabilities
                    TimeSlot slot = new TimeSlot();
                    slot.setStartTime(start);
                    // Сохраняем TimeSlot, чтобы получить id
                    slot = slotRepo.save(slot);

                    // Подготовим availabilities
                    List<DoctorAvailability> availList = new ArrayList<>();
                    for (Doctor doc : doctors) {
                        DoctorAvailability av = new DoctorAvailability();
                        av.setSlotId(slot.getId()); // Теперь id доступен
                        av.setDoctor(doc);
                        av.setAvailable(true);
                        availList.add(av);
                    }

                    // Устанавливаем availabilities и повторно сохраняем TimeSlot
                    slot.setAvailabilities(availList);
                    slotRepo.save(slot);
                }
            }

            System.out.println("=== Инициализация данных завершена ===");
        };
    }
}

