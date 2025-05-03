package org.vetclinic.recommendationservice.repository;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.vetclinic.recommendationservice.model.Reminder;
import org.vetclinic.recommendationservice.model.ReminderStatus;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReminderRepository extends Neo4jRepository<Reminder, Long> {

    @Query("MATCH (userPet:Pet {ownerId: $userId})<-[:REMINDER_FOR]-(r:Reminder) RETURN r")
    List<Reminder> findByPetOwnerId(@Param("userId") Long userId);

    @Query("MATCH (userPet:Pet {ownerId: $userId})<-[:REMINDER_FOR]-(r:Reminder) WHERE r.status = $status RETURN r")
    List<Reminder> findByPetOwnerIdAndStatus(@Param("userId") Long userId, @Param("status") ReminderStatus status);

    @Query("MATCH (userPet:Pet {ownerId: $userId})<-[:REMINDER_FOR]-(r:Reminder) WHERE ID(r) = $reminderId RETURN r")
    Optional<Reminder> findByIdAndPetOwnerId(@Param("reminderId") Long reminderId, @Param("userId") Long userId);

}
