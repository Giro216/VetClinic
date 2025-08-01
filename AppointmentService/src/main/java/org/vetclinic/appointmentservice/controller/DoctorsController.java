package org.vetclinic.appointmentservice.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.vetclinic.appointmentservice.model.Doctor;
import org.vetclinic.appointmentservice.repository.DoctorRepository;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/appointments/doctors")
@CrossOrigin(origins = "http://localhost:3001", allowCredentials = "true")
public class DoctorsController {
    DoctorRepository doctorRepository;

    @GetMapping
    public ResponseEntity<List<Doctor>> getAllDoctors() {
        List<Doctor> doctors = (List<Doctor>) doctorRepository.findAll();
        if (doctors.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(doctors, HttpStatus.OK);
    }
}
