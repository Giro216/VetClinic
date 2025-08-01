package org.vetclinic.recommendationservice.repository;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;
import org.vetclinic.recommendationservice.model.Ingredient;

@Repository
public interface IngredientRepository extends Neo4jRepository<Ingredient, String> {
}
