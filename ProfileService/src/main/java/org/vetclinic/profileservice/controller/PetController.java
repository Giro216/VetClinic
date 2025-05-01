package org.vetclinic.profileservice.controller;

import lombok.RequiredArgsConstructor;
import org.vetclinic.profileservice.model.Pet;
import org.vetclinic.profileservice.service.PetService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pets")
@RequiredArgsConstructor
public class PetController {

    private final PetService petService;

    @GetMapping("/all")
    public List<Pet> getAllPets(){
        return petService.getAllPets();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Pet> getPetById(@PathVariable String id) {
        return petService.getPetById(id);
    }

    @PostMapping
    public ResponseEntity<Pet> savePet(@RequestBody Pet pet) {
        return ResponseEntity.ok(petService.putPet(pet));
    }
}
