package org.vetclinic.profileservice.controller;

import lombok.RequiredArgsConstructor;
import org.vetclinic.profileservice.model.MedCard;
import org.vetclinic.profileservice.model.Pet;
import org.vetclinic.profileservice.service.MedCardService;
import org.vetclinic.profileservice.service.PetService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pets")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3001", allowCredentials = "true")
public class PetController {

    private final PetService petService;
    private final MedCardService cardService;

    @GetMapping("/all")
    public List<Pet> getAllPets(){
        return petService.getAllPets();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Pet> getPet(@PathVariable String id) {
        return petService.getPetById(id);
    }

    @PostMapping
    public ResponseEntity<Pet> postPet(@RequestBody Pet pet) {
        Pet savedPet = petService.savePet(pet);

        ResponseEntity<MedCard> cardResponse = cardService.createCard(savedPet.getPetId());
        if (!cardResponse.getStatusCode().is2xxSuccessful()) {
            return ResponseEntity.status(cardResponse.getStatusCode()).build();
        }

        return ResponseEntity.ok(savedPet);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Pet> putPet(@PathVariable String id, @RequestBody Pet pet) {
        return ResponseEntity.ok(petService.updatePet(id, pet));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePet(@PathVariable String id) {
        petService.deletePet(id);
        return ResponseEntity.noContent().build();
    }
}
