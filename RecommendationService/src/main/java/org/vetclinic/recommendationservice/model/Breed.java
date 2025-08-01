package org.vetclinic.recommendationservice.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import static org.springframework.data.neo4j.core.schema.Relationship.Direction.OUTGOING;

@Getter
@Setter
@Node("Breed")
@NoArgsConstructor
public class Breed {

    @Id
    private String name;

    @Relationship(type = "PART_OF_SPECIES", direction = OUTGOING)
    private Species species;

}
