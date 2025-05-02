package org.vetclinic.profileservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.vetclinic.profileservice.model.Pet;
import org.vetclinic.profileservice.repository.PetRepo;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PetService {

    private final PetRepo petRepo;
    private final MedCardService medCardService;

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

    public Pet savePet(Pet pet) {
        pet.setPetId(UUID.randomUUID().toString());
        return petRepo.save(pet);
    }

    public Pet updatePet(String id, Pet pet) {
        petRepo.findById(id);
        return petRepo.save(pet);
    }

    @Transactional
    public void deletePet(String id) {
        Pet pet = petRepo.findByPetId(id);
        if (pet != null) {
            medCardService.deleteCard(id);
            petRepo.deleteById(id);
        }
    }
}
