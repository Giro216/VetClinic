package org.vetclinic.recommendationservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.vetclinic.recommendationservice.dto.mapper.CareTipMapper;
import org.vetclinic.recommendationservice.dto.request.CareTipCreateRequestDto;
import org.vetclinic.recommendationservice.dto.request.CareTipUpdateRequestDto;
import org.vetclinic.recommendationservice.dto.response.CareTipResponseDto;
import org.vetclinic.recommendationservice.exception.NotFoundException;
import org.vetclinic.recommendationservice.model.*;
import org.vetclinic.recommendationservice.repository.*;

import java.time.LocalDate;
import java.time.Period;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class CareTipServiceImpl implements CareTipService {

    private final PetRepository petRepository;

    private final CareTipRepository careTipRepository;

    private final SpeciesRepository speciesRepository;

    private final BreedRepository breedRepository;

    private final AllergyRepository allergyRepository;

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

    @Override
    public List<CareTipResponseDto> getAllCareTips() {
        log.debug("Fetching all care tips");
        return careTipRepository.findAll().stream()
                .map(careTipMapper::toCareTipResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public CareTipResponseDto getCareTipById(UUID tipId) {
        log.debug("Fetching care tip with id: {}", tipId);
        CareTip tip = careTipRepository.findById(tipId)
                .orElseThrow(() -> new NotFoundException("CareTip with id " + tipId + " not found"));

        return careTipMapper.toCareTipResponseDto(tip);
    }

    @Override
    public CareTipResponseDto createCareTip(CareTipCreateRequestDto dto) {
        log.info("Creating new care tip with title: {}", dto.title());
        CareTip careTip = careTipMapper.toCareTip(dto);

        careTip.setApplicableSpecies(resolveSpecies(dto.applicableSpeciesNames()));
        careTip.setApplicableBreeds(resolveBreeds(dto.applicableBreedNames()));
        careTip.setRelevantAllergies(resolveAllergies(dto.relevantAllergyNames()));

        CareTip savedTip = careTipRepository.save(careTip);
        log.info("Care tip created with id: {}", savedTip.getTipId());
        return careTipMapper.toCareTipResponseDto(savedTip);
    }

    @Override
    public CareTipResponseDto updateCareTip(UUID tipId, CareTipUpdateRequestDto dto) {
        log.info("Updating care tip with id: {}", tipId);
        CareTip existingTip = careTipRepository.findById(tipId)
                .orElseThrow(() -> new NotFoundException("CareTip with id " + tipId + " not found"));

        careTipMapper.partialUpdate(dto, existingTip);

        if (dto.applicableSpeciesNames() != null) {
            existingTip.setApplicableSpecies(resolveSpecies(dto.applicableSpeciesNames()));
        }
        if (dto.applicableBreedNames() != null) {
            existingTip.setApplicableBreeds(resolveBreeds(dto.applicableBreedNames()));
        }
        if (dto.relevantAllergyNames() != null) {
            existingTip.setRelevantAllergies(resolveAllergies(dto.relevantAllergyNames()));
        }

        if (dto.minAgeMonths() == null && existingTip.getMinAgeMonths() != null) {
            existingTip.setMinAgeMonths(null);
        }
        if (dto.maxAgeMonths() == null && existingTip.getMaxAgeMonths() != null) {
            existingTip.setMaxAgeMonths(null);
        }

        CareTip updatedTip = careTipRepository.save(existingTip);
        log.info("Care tip updated successfully: {}", updatedTip.getTipId());
        return careTipMapper.toCareTipResponseDto(updatedTip);
    }

    @Override
    public void deleteCareTip(UUID tipId) {
        log.warn("Attempting to delete care tip with id: {}", tipId);
        if (!careTipRepository.existsById(tipId)) {
            throw new NotFoundException("CareTip with id " + tipId + " not found");
        }

        careTipRepository.deleteById(tipId);
        log.info("Care tip deleted successfully: {}", tipId);
    }

    private Integer calculateAgeInMonths(LocalDate birthDate) {
        if (birthDate == null) {
            return null;
        }
        return Period.between(birthDate, LocalDate.now()).getYears() * 12 +
                Period.between(birthDate, LocalDate.now()).getMonths();
    }

    private Set<Species> resolveSpecies(Set<String> names) {
        if (names == null || names.isEmpty()) return Collections.emptySet();
        Set<Species> resolved = new HashSet<>();
        for (String name : names) {
            resolved.add(speciesRepository.findById(name).orElseGet(() -> {
                log.warn("Species '{}' not found, creating new.", name);
                Species s = new Species();
                s.setName(name);
                return speciesRepository.save(s);
            }));
        }
        return resolved;
    }

    private Set<Breed> resolveBreeds(Set<String> names) {
        if (names == null || names.isEmpty()) return Collections.emptySet();
        Set<Breed> resolved = new HashSet<>();
        for (String name : names) {
            resolved.add(breedRepository.findById(name).orElseGet(() -> {
                log.warn("Breed '{}' not found, creating new (without species link!).", name);
                Breed b = new Breed();
                b.setName(name);
                return breedRepository.save(b);
            }));
        }
        return resolved;
    }

    private Set<Allergy> resolveAllergies(Set<String> names) {
        if (names == null || names.isEmpty()) return Collections.emptySet();
        Set<Allergy> resolved = new HashSet<>();
        for (String name : names) {
            if (org.springframework.util.StringUtils.hasText(name)) {
                resolved.add(allergyRepository.findById(name).orElseGet(() -> {
                    log.warn("Allergy '{}' not found, creating new.", name);
                    Allergy a = new Allergy();
                    a.setName(name);
                    return allergyRepository.save(a);
                }));
            }
        }
        return resolved;
    }

}
