package org.vetclinic.profileservice.service;

import lombok.RequiredArgsConstructor;
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

    public List<MedCard> getAllCards(){
        return medCardRepo.findAll();
    }

    public ResponseEntity<MedCard> getCardById(String id){
        var card = medCardRepo.findByPetId(id);
        if (card == null){
            return ResponseEntity.status(HttpStatusCode.valueOf(404)).build();
        }

        return new ResponseEntity<>(card, HttpStatusCode.valueOf(200));
    }

    public MedCard putCard(MedCard card) {
        card.setPetId(UUID.randomUUID().toString());
        return medCardRepo.save(card);
    }

}
