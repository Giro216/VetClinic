package org.vetclinic.appointmentservice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/appointments")
@CrossOrigin(origins = "http://localhost:3001", allowCredentials = "true")
public class GatewayController {

    @GetMapping("/hello")
    public ResponseEntity<String> getAnonymous() {
        return ResponseEntity.ok("Welcome to GeeksforGeeks");
    }
}