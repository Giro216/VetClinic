package org.vetclinic.recommendationservice.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.HashSet;
import java.util.Set;

import static org.springframework.data.neo4j.core.schema.Relationship.Direction.OUTGOING;

@Getter
@Setter
@Node("Allergy")
@NoArgsConstructor
public class Allergy {

    @Id
    private String name;

    private String description;

    @Relationship(type = "RELATED_TO_INGREDIENT", direction = OUTGOING)
    private Set<Ingredient> relatedIngredients = new HashSet<>();

}
