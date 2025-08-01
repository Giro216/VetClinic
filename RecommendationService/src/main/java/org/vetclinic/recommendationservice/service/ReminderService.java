package org.vetclinic.recommendationservice.service;

import org.vetclinic.recommendationservice.dto.request.ReminderCreationRequestDto;
import org.vetclinic.recommendationservice.dto.request.ReminderUpdateRequestDto;
import org.vetclinic.recommendationservice.dto.response.ReminderResponseDto;
import org.vetclinic.recommendationservice.model.ReminderStatus;

import java.util.List;

public interface ReminderService {

    List<ReminderResponseDto> getRemindersForUser(Long userId, ReminderStatus status);

    ReminderResponseDto getReminderById(Long userId, Long reminderId);

    ReminderResponseDto createReminder(Long userId, ReminderCreationRequestDto request);

    ReminderResponseDto updateReminder(Long userId, Long reminderId, ReminderUpdateRequestDto updateRequest);

    void deleteReminder(Long userId, Long reminderId);

}
