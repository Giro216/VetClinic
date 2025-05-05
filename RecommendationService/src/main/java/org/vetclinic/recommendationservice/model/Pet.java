package org.vetclinic.recommendationservice.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.springframework.data.neo4j.core.schema.Relationship.Direction.OUTGOING;

@Getter
@Setter
@Node("Pet")
@NoArgsConstructor
public class Pet {

    @Id
    @GeneratedValue(GeneratedValue.UUIDGenerator.class)
    private UUID petId;

    private Long ownerId;

    private String name;

    private LocalDate birthDate;

    @Relationship(type = "BELONGS_TO_SPECIES", direction = OUTGOING)
    private Species species;

    @Relationship(type = "IS_BREED", direction = OUTGOING)
    private Breed breed;

    @Relationship(type = "HAS_ALLERGY", direction = OUTGOING)
    private Set<Allergy> allergies = new HashSet<>();

}
