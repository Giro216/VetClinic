package org.vetclinic.recommendationservice.repository;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;
import org.vetclinic.recommendationservice.model.Pet;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PetRepository extends Neo4jRepository<Pet, UUID> {

    Optional<Pet> findByPetIdAndOwnerId(UUID petId, Long ownerId);

    List<Pet> findByOwnerId(Long ownerId);

}
