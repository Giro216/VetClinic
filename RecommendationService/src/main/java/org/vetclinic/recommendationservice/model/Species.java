package org.vetclinic.recommendationservice.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.neo4j.core.schema.Node;

@Getter
@Setter
@Node("Species")
@NoArgsConstructor
public class Species {

    @Id
    private String name;

}
