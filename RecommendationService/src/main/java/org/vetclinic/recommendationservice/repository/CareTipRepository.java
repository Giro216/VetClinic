package org.vetclinic.recommendationservice.repository;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.vetclinic.recommendationservice.model.CareTip;

import java.util.List;
import java.util.UUID;

@Repository
public interface CareTipRepository extends Neo4jRepository<CareTip, UUID> {

    @Query("""
            MATCH (p:Pet {petId: $petId})
            WITH p, $ageInMonths AS ageInMonths
            MATCH (p)-[:BELONGS_TO_SPECIES]->(s:Species)<-[:APPLIES_TO_SPECIES]-(ct:CareTip)
            WHERE (ageInMonths >= coalesce(ct.minAgeMonths, 0))
              AND (ageInMonths <= coalesce(ct.maxAgeMonths, 99999))
            WITH p, ct
            OPTIONAL MATCH (p)-[:IS_BREED]->(petBreed:Breed)
            OPTIONAL MATCH (ct)-[:APPLIES_TO_BREED]->(tipBreed:Breed)
            WITH p, ct, petBreed, tipBreed
            WHERE tipBreed IS NULL OR petBreed = tipBreed
            WITH p, ct
            OPTIONAL MATCH (ct)-[:RELEVANT_FOR_ALLERGY]->(tipAllergy:Allergy)
            WITH p, ct, COLLECT(DISTINCT tipAllergy) AS relevantAllergies
            OPTIONAL MATCH (p)-[:HAS_ALLERGY]->(petAllergy:Allergy)
            WITH ct, relevantAllergies, COLLECT(DISTINCT petAllergy) AS petAllergies
            WHERE size(relevantAllergies) = 0
               OR size([a IN relevantAllergies WHERE a IN petAllergies]) > 0
            RETURN DISTINCT ct
            ORDER BY ct.category, ct.title
            """)
    List<CareTip> findRecommendationsForPet(@Param("petId") UUID petId, @Param("ageInMonths") Integer ageInMonths);

}
