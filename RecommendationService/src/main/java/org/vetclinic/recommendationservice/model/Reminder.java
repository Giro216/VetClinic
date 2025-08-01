package org.vetclinic.recommendationservice.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.time.LocalDate;

import static org.springframework.data.neo4j.core.schema.Relationship.Direction.OUTGOING;

@Getter
@Setter
@Node("Reminder")
@NoArgsConstructor
public class Reminder {

    @Id
    @GeneratedValue
    private Long id;

    private String type;

    private String description;

    private LocalDate dueDate;

    private ReminderStatus status = ReminderStatus.PENDING;

    @Relationship(type = "REMINDER_FOR", direction = OUTGOING)
    private Pet pet;

}
