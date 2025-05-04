package org.vetclinic.appointmentservice.dto;

import org.vetclinic.appointmentservice.model.Appointment;
import org.springframework.stereotype.Component;
import org.vetclinic.appointmentservice.model.AppointmentStatus;
import org.vetclinic.appointmentservice.model.Doctor;
import org.vetclinic.appointmentservice.model.TimeSlot;
import org.vetclinic.appointmentservice.repository.DoctorRepository;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Optional;

@Component
public class ManualAppointmentMapper {

    private final DoctorRepository doctorRepository;

    public ManualAppointmentMapper(DoctorRepository doctorRepository) {
        this.doctorRepository = doctorRepository;
    }

    /**
     * Преобразует AppointmentRequestDto в сущность Appointment.
     * Требуются объекты Doctor и TimeSlot для заполнения полей doctorName и slot.
     *
     * @param dto    Входной DTO с данными запроса на создание назначения
     * @param slot   Сущность временного слота, полученная по requiredSlotId
     * @return Сформированная сущность Appointment
     */
    public Appointment toEntity(AppointmentRequestDto dto, TimeSlot slot) {
        Appointment appointment = new Appointment();

        appointment.setPetId(dto.petId().longValue());
        appointment.setDoctorId(dto.doctorId());
        appointment.setSlot(slot);
        appointment.setStatus(AppointmentStatus.BOOKED);
        appointment.setReason(dto.reason());

        return appointment;
    }

    /**
     * Преобразует сущность Appointment в AppointmentResponseDto.
     *
     * @param appointment Сущность Appointment
     * @return DTO с данными для ответа клиенту
     */
    public AppointmentResponseDto toDto(Appointment appointment) {
        // Получаем startTime из TimeSlot и преобразуем в OffsetDateTime с учетом системной временной зоны
        LocalDateTime startTime = appointment.getSlot().getStartTime();
        OffsetDateTime datetime = startTime.atZone(ZoneId.systemDefault()).toOffsetDateTime();

        Optional<Doctor> doctor = doctorRepository.findById(appointment.getDoctorId());
        String doctorName = null;
        if (doctor.isPresent()) {
            doctorName = doctor.get().getName();
        }

        return new AppointmentResponseDto(
                appointment.getId(),                    // ID назначения
                appointment.getPetId(),                 // ID питомца
                doctorName,                             // Имя доктора
                datetime,                               // Дата и время из слота
                appointment.getStatus().name(),         // Статус в виде строки
                appointment.getCreatedAt()              // Время создания
        );
    }
}
