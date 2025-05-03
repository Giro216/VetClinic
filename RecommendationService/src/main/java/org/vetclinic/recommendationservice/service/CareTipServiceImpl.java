package org.vetclinic.recommendationservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.vetclinic.recommendationservice.dto.mapper.CareTipMapper;
import org.vetclinic.recommendationservice.dto.response.CareTipResponseDto;
import org.vetclinic.recommendationservice.exception.NotFoundException;
import org.vetclinic.recommendationservice.model.CareTip;
import org.vetclinic.recommendationservice.model.Pet;
import org.vetclinic.recommendationservice.repository.CareTipRepository;
import org.vetclinic.recommendationservice.repository.PetRepository;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class CareTipServiceImpl implements CareTipService {

    private final PetRepository petRepository;

    private final CareTipRepository careTipRepository;

    private final CareTipMapper careTipMapper;

    @Override
    public List<CareTipResponseDto> getCareTipsForPet(UUID petId) {
        log.debug("Fetching care tips recommendations for petId: {}", petId);

        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new NotFoundException("Pet with id " + petId + " not found"));

        Integer ageInMonths = calculateAgeInMonths(pet.getBirthDate());
        log.debug("Calculated age in months for pet {}: {}", petId, ageInMonths);
        if (ageInMonths == null) {
            log.warn("Pet {} has no birth date, applying recommendations for 'adult' (using high age value)", petId);
            ageInMonths = Integer.MAX_VALUE / 12;
        }

        List<CareTip> tips = careTipRepository.findRecommendationsForPet(petId, ageInMonths);
        log.info("Found {} care tips for pet {}", tips.size(), petId);

        return tips.stream()
                .map(careTipMapper::toCareTipResponseDto)
                .collect(Collectors.toList());
    }

    private Integer calculateAgeInMonths(LocalDate birthDate) {
        if (birthDate == null) {
            return null;
        }
        return Period.between(birthDate, LocalDate.now()).getYears() * 12 +
                Period.between(birthDate, LocalDate.now()).getMonths();
    }

}
