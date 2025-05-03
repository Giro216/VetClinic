package org.vetclinic.recommendationservice.repository;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.vetclinic.recommendationservice.model.Breed;

import java.util.Optional;

@Repository
public interface BreedRepository extends Neo4jRepository<Breed, String> {

    @Query("MATCH (b:Breed {name: $breedName})-[:PART_OF_SPECIES]->(s:Species {name: $speciesName}) RETURN b")
    Optional<Breed> findByNameAndSpeciesName(@Param("breedName") String breedName,
                                             @Param("speciesName") String speciesName);

}
