package org.vetclinic.recommendationservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.vetclinic.recommendationservice.dto.mapper.ReminderMapper;
import org.vetclinic.recommendationservice.dto.request.ReminderCreationRequestDto;
import org.vetclinic.recommendationservice.dto.request.ReminderUpdateRequestDto;
import org.vetclinic.recommendationservice.dto.response.ReminderResponseDto;
import org.vetclinic.recommendationservice.exception.NotFoundException;
import org.vetclinic.recommendationservice.model.Pet;
import org.vetclinic.recommendationservice.model.Reminder;
import org.vetclinic.recommendationservice.model.ReminderStatus;
import org.vetclinic.recommendationservice.repository.PetRepository;
import org.vetclinic.recommendationservice.repository.ReminderRepository;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ReminderServiceImpl implements ReminderService {

    private final ReminderRepository reminderRepository;

    private final PetRepository petRepository;

    private final ReminderMapper reminderMapper;

    @Override
    public List<ReminderResponseDto> getRemindersForUser(Long userId, ReminderStatus status) {
        log.debug("Fetching reminders for user {} with status {}", userId, status);

        List<Reminder> reminders = (status != null)
                ? reminderRepository.findByPetOwnerIdAndStatus(userId, status)
                : reminderRepository.findByPetOwnerId(userId);

        log.info("Found {} reminders for user {}", reminders.size(), userId);
        return reminders.stream()
                .map(reminderMapper::toReminderResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public ReminderResponseDto getReminderById(Long userId, Long reminderId) {
        log.debug("Fetching reminder {} for user {}", reminderId, userId);
        Reminder reminder = reminderRepository.findByIdAndPetOwnerId(reminderId, userId)
                .orElseThrow(() -> new NotFoundException("Reminder with id " + reminderId + " not found " +
                        "or not owned by user " + userId));
        return reminderMapper.toReminderResponseDto(reminder);
    }

    @Override
    public ReminderResponseDto createReminder(Long userId, ReminderCreationRequestDto creationRequest) {
        log.info("Creating reminder for user {} and pet {}", userId, creationRequest.petId());

        Pet pet = petRepository.findByPetIdAndOwnerId(creationRequest.petId(), userId)
                .orElseThrow(() -> new NotFoundException("Pet with id " + creationRequest.petId() + " not found " +
                        "or not owned by user " + userId));

        Reminder savedReminder = reminderRepository.save(reminderMapper.toReminder(creationRequest, pet));
        log.info("Reminder created with id {}", savedReminder.getId());
        return reminderMapper.toReminderResponseDto(savedReminder);
    }

    @Override
    public ReminderResponseDto updateReminder(Long userId, Long reminderId, ReminderUpdateRequestDto updateRequest) {
        log.info("Updating reminder {} for user {}", reminderId, userId);

        Reminder existingReminder = reminderRepository.findByIdAndPetOwnerId(reminderId, userId)
                .orElseThrow(() -> new NotFoundException("Reminder with id " + reminderId + " not found " +
                        "or not owned by user " + userId));

        reminderMapper.partialUpdate(updateRequest, existingReminder);
        Reminder updatedReminder = reminderRepository.save(existingReminder);
        log.info("Reminder {} updated successfully", updatedReminder.getId());
        return reminderMapper.toReminderResponseDto(updatedReminder);
    }

    @Override
    public void deleteReminder(Long userId, Long reminderId) {
        log.info("Deleting reminder {} for user {}", reminderId, userId);

        Reminder reminderToDelete = reminderRepository.findByIdAndPetOwnerId(reminderId, userId)
                .orElseThrow(() -> new NotFoundException("Reminder with id " + reminderId + " not found " +
                        "or not owned by user " + userId));

        reminderRepository.deleteById(reminderToDelete.getId());
        log.info("Reminder {} deleted successfully", reminderId);
    }

}
