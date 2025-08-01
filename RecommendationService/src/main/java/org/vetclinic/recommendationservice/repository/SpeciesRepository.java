package org.vetclinic.recommendationservice.repository;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;
import org.vetclinic.recommendationservice.model.Species;

@Repository
public interface SpeciesRepository extends Neo4jRepository<Species, String> {
}
