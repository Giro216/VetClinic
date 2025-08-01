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
@Node("Food")
@NoArgsConstructor
public class Food {

    @Id
    @GeneratedValue(GeneratedValue.UUIDGenerator.class)
    private UUID foodId;

    private String name;

    private String brand;

    private String description;

    private Integer minAgeMonths;

    private Integer maxAgeMonths;

    @Relationship(type = "TARGETS_SPECIES", direction = OUTGOING)
    private Set<Species> targetSpecies = new HashSet<>();

    @Relationship(type = "TARGETS_BREED", direction = OUTGOING)
    private Set<Breed> targetBreeds = new HashSet<>();

    @Relationship(type = "CONTAINS_INGREDIENT", direction = OUTGOING)
    private Set<Ingredient> ingredients = new HashSet<>();

}
