package org.vetclinic.profileservice.repository;

import org.vetclinic.profileservice.model.Pet;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PetRepo extends MongoRepository<Pet, String> {
    Pet findByPetId(String petId);
}
