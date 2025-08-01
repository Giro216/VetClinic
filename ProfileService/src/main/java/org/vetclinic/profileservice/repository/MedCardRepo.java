package org.vetclinic.profileservice.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.vetclinic.profileservice.model.MedCard;

public interface MedCardRepo extends MongoRepository<MedCard, String> {
    MedCard findByPetId(String petId);
}
