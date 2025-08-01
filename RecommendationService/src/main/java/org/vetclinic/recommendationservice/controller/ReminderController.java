package org.vetclinic.recommendationservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.vetclinic.recommendationservice.dto.request.ReminderCreationRequestDto;
import org.vetclinic.recommendationservice.dto.request.ReminderUpdateRequestDto;
import org.vetclinic.recommendationservice.dto.response.ReminderResponseDto;
import org.vetclinic.recommendationservice.model.ReminderStatus;
import org.vetclinic.recommendationservice.service.ReminderService;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/users/{userId}/reminders")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3001", allowCredentials = "true")
public class ReminderController {

    private final ReminderService reminderService;

    @GetMapping
    public ResponseEntity<List<ReminderResponseDto>> getReminders(@PathVariable Long userId,
                                                                  @RequestParam(required = false) ReminderStatus status) {
        return ResponseEntity.ok(reminderService.getRemindersForUser(userId, status));
    }

    @PostMapping
    public ResponseEntity<ReminderResponseDto> createReminder(@PathVariable Long userId,
                                                              @Valid @RequestBody ReminderCreationRequestDto requestDto) {
        var createdReminder = reminderService.createReminder(userId, requestDto);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{reminderId}")
                .buildAndExpand(userId, createdReminder.id())
                .toUri();

        return ResponseEntity.created(location).body(createdReminder);
    }

    @GetMapping("/{reminderId}")
    public ResponseEntity<ReminderResponseDto> getReminder(@PathVariable Long userId, @PathVariable Long reminderId) {
        return ResponseEntity.ok(reminderService.getReminderById(userId, reminderId));
    }

    @PatchMapping("/{reminderId}")
    public ResponseEntity<ReminderResponseDto> updateReminder(@PathVariable Long userId,
                                                              @PathVariable Long reminderId,
                                                              @Valid @RequestBody ReminderUpdateRequestDto requestDto) {
        var updatedReminder = reminderService.updateReminder(userId, reminderId, requestDto);
        return ResponseEntity.ok().body(updatedReminder);
    }

    @DeleteMapping("/{reminderId}")
    public ResponseEntity<Void> deleteReminder(@PathVariable Long userId, @PathVariable Long reminderId) {
        reminderService.deleteReminder(userId, reminderId);
        return ResponseEntity.noContent().build();
    }

}
