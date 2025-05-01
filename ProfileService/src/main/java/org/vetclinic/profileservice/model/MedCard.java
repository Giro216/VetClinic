package org.vetclinic.profileservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document
@Getter
@Setter
@Builder
public class MedCard {

    @Id
    private String petId;

    private List<Vaccination> vaccinations;
    private List<String> allergies;
    private List<String> diseases;

    @Getter
    @Setter
    @AllArgsConstructor
    public static class Vaccination {
        private String date;
        private String type;
    }
}
