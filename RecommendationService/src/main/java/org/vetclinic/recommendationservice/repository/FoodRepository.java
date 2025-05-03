package org.vetclinic.recommendationservice.repository;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.vetclinic.recommendationservice.model.Food;

import java.util.List;
import java.util.UUID;

@Repository
public interface FoodRepository extends Neo4jRepository<Food, UUID> {

    @Query("""
            MATCH (p:Pet {petId: $petId})
            WITH p, $ageInMonths AS ageInMonths
            OPTIONAL MATCH (p)-[:HAS_ALLERGY]->(pa:Allergy)-[:RELATED_TO_INGREDIENT]->(allergyIngredient:Ingredient)
            WITH p, ageInMonths, COLLECT(DISTINCT allergyIngredient.name) AS forbiddenIngredientNames
            MATCH (f:Food)-[:TARGETS_SPECIES]->(s:Species)<-[:BELONGS_TO_SPECIES]-(p)
            WHERE (f.minAgeMonths IS NULL OR f.minAgeMonths <= ageInMonths)
              AND (f.maxAgeMonths IS NULL OR $ageInMonths <= f.maxAgeMonths)
            WITH p, f, forbiddenIngredientNames
            OPTIONAL MATCH (p)-[:IS_BREED]->(petBreed:Breed)
            OPTIONAL MATCH (f)-[:TARGETS_BREED]->(foodBreed:Breed)
            WITH f, petBreed, foodBreed, forbiddenIngredientNames
            WHERE foodBreed IS NULL OR petBreed = foodBreed
            WITH f, forbiddenIngredientNames
            WHERE NOT EXISTS { MATCH (f)-[:CONTAINS_INGREDIENT]->(i:Ingredient) WHERE i.name IN forbiddenIngredientNames }
            RETURN DISTINCT f ORDER BY f.brand, f.name
            """)
    List<Food> findRecommendationsForPet(@Param("petId") UUID petId, @Param("ageInMonths") Integer ageInMonths);

}
