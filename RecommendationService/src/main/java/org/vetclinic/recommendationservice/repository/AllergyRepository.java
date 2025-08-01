package org.vetclinic.recommendationservice.repository;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;
import org.vetclinic.recommendationservice.model.Allergy;

@Repository
public interface AllergyRepository extends Neo4jRepository<Allergy, String> {
}
