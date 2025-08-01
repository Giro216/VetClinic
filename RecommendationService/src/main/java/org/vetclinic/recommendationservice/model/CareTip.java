package org.vetclinic.recommendationservice.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.springframework.data.neo4j.core.schema.Relationship.Direction.OUTGOING;

@Getter
@Setter
@Node("CareTip")
@NoArgsConstructor
public class CareTip {

    @Id
    @GeneratedValue(GeneratedValue.UUIDGenerator.class)
    private UUID tipId;

    private String title;

    private String content;

    private String category;

    private Integer minAgeMonths;

    private Integer maxAgeMonths;

    @Relationship(type = "APPLIES_TO_SPECIES", direction = OUTGOING)
    private Set<Species> applicableSpecies = new HashSet<>();

    @Relationship(type = "APPLIES_TO_BREED", direction = OUTGOING)
    private Set<Breed> applicableBreeds = new HashSet<>();

    @Relationship(type = "RELEVANT_FOR_ALLERGY", direction = OUTGOING)
    private Set<Allergy> relevantAllergies = new HashSet<>();

}
