package org.vetclinic.profileservice.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document(collection = "pets")
public class Pet {

    @Id
    private String petId;

    private String name;

    private int age;

    private String kind;
}
