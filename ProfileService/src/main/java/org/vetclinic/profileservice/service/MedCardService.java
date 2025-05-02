package org.vetclinic.profileservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.vetclinic.profileservice.model.MedCard;
import org.vetclinic.profileservice.model.Pet;
import org.vetclinic.profileservice.repository.MedCardRepo;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MedCardService {

    private final MedCardRepo medCardRepo;

    public List<MedCard> getAllCards() {
        return medCardRepo.findAll();
    }

    public ResponseEntity<MedCard> getCardById(String id) {
        var card = medCardRepo.findByPetId(id);
        if (card == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(card);
    }

    public ResponseEntity<MedCard> createCard(String petId) {
        if (medCardRepo.findByPetId(petId) != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
        }
        MedCard medCard = MedCard.builder()
                .petId(petId)
                .vaccinations(List.of())
                .allergies(List.of())
                .diseases(List.of())
                .build();
        MedCard savedCard = medCardRepo.save(medCard);
        return ResponseEntity.ok(savedCard);
    }

    public ResponseEntity<MedCard> updateCard(String petId, MedCard updatedCard) {
        var target = medCardRepo.findByPetId(petId);
        if (target == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        target.setVaccinations(updatedCard.getVaccinations());
        target.setAllergies(updatedCard.getAllergies());
        target.setDiseases(updatedCard.getDiseases());

        MedCard savedCard = medCardRepo.save(target);
        return ResponseEntity.ok(savedCard);
    }

    public ResponseEntity<Void> deleteCard(String petId) {
        var card = medCardRepo.findByPetId(petId);
        if (card == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        medCardRepo.delete(card);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
