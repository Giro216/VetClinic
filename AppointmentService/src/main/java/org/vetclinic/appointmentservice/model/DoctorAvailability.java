package org.vetclinic.appointmentservice.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "doctor_availability",
        uniqueConstraints = @UniqueConstraint(columnNames = {"slot_id", "doctor_id"}))
public class DoctorAvailability {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "slot_id", nullable = false)
    //todo я поменял тип данных со slot на slotId и поэтому надо починить зависимости
    private Long slotId;

    @ManyToOne
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    @Column(nullable = false)
    private boolean available;
}

