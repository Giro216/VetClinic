package org.vetclinic.recommendationservice.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

@Getter
@Setter
@Node("Ingredient")
@NoArgsConstructor
public class Ingredient {

    @Id
    private String name;

}
