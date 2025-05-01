package org.vetclinic.profileservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.vetclinic.profileservice.model.MedCard;
import org.vetclinic.profileservice.model.Pet;
import org.vetclinic.profileservice.service.MedCardService;

import java.util.List;

@RestController
@RequestMapping("/api/cards")
@RequiredArgsConstructor
public class MedCardController {

    private final MedCardService cardService;

    @GetMapping("/all")
    public List<MedCard> getAllCards() {
        return cardService.getAllCards();
    }

    @GetMapping("/{id}")
    public ResponseEntity<MedCard> getCardById(@PathVariable String id) {
        return cardService.getCardById(id);
    }

}
