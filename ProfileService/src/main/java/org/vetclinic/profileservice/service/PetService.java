package org.vetclinic.profileservice.service;

import lombok.RequiredArgsConstructor;
import org.vetclinic.profileservice.model.Pet;
import org.vetclinic.profileservice.repository.PetRepo;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PetService {

    private final PetRepo petRepo;

    public List<Pet> getAllPets(){
        return petRepo.findAll();
    }

    public ResponseEntity<Pet> getPetById(String id){
        var pet = petRepo.findByPetId(id);
        if (pet == null){
            return ResponseEntity.status(HttpStatusCode.valueOf(404)).build();
        }

        return new ResponseEntity<>(pet, HttpStatusCode.valueOf(200));
    }

    public Pet putPet(Pet pet) {
        pet.setPetId(UUID.randomUUID().toString());
        return petRepo.save(pet);
    }
}
