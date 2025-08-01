package org.vetclinic.recommendationservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.vetclinic.recommendationservice.dto.mapper.FoodMapper;
import org.vetclinic.recommendationservice.dto.request.FoodCreateRequestDto;
import org.vetclinic.recommendationservice.dto.request.FoodUpdateRequestDto;
import org.vetclinic.recommendationservice.dto.response.FoodRecommendationResponseDto;
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
public class FoodRecommendationServiceImpl implements FoodRecommendationService {

    private final PetRepository petRepository;

    private final FoodRepository foodRepository;

    private final SpeciesRepository speciesRepository;

    private final BreedRepository breedRepository;

    private final FoodMapper foodMapper;

    private final IngredientRepository ingredientRepository;

    @Override
    public List<FoodRecommendationResponseDto> getFoodRecommendationsForPet(UUID petId) {
        log.debug("Fetching food recommendations for petId: {}", petId);

        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new NotFoundException("Pet with id " + petId + " not found"));

        Integer ageInMonths = calculateAgeInMonths(pet.getBirthDate());
        log.debug("Calculated age in months for pet {}: {}", petId, ageInMonths);
        if (ageInMonths == null) {
            log.warn("Pet {} has no birth date, applying recommendations for 'adult' (using high age value)", petId);
            ageInMonths = Integer.MAX_VALUE / 12;
        }

        List<Food> recommendations = foodRepository.findRecommendationsForPet(petId, ageInMonths);
        log.info("Found {} food recommendations for pet {}", recommendations.size(), petId);

        return recommendations.stream()
                .map(foodMapper::toFoodRecommendationResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<FoodRecommendationResponseDto> getAllFood() {
        log.debug("Fetching all food");

        return foodRepository.findAll().stream()
                .map(foodMapper::toFoodRecommendationResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public FoodRecommendationResponseDto getFoodById(UUID foodId) {
        log.debug("Fetching food with id: {}", foodId);
        Food food = foodRepository.findById(foodId)
                .orElseThrow(() -> new NotFoundException("Food with id " + foodId + " not found"));

        return foodMapper.toFoodRecommendationResponseDto(food);
    }

    @Override
    public FoodRecommendationResponseDto createFood(FoodCreateRequestDto dto) {
        log.info("Creating new food with name: {}", dto.name());
        Food food = foodMapper.toFood(dto);

        food.setTargetSpecies(resolveSpecies(dto.targetSpeciesNames()));
        food.setTargetBreeds(resolveBreeds(dto.targetBreedNames()));
        food.setIngredients(resolveIngredients(dto.ingredientNames()));

        Food savedFood = foodRepository.save(food);
        log.info("Food created with id: {}", savedFood.getFoodId());
        return foodMapper.toFoodRecommendationResponseDto(savedFood);
    }

    @Override
    public FoodRecommendationResponseDto updateFood(UUID foodId, FoodUpdateRequestDto dto) {
        log.info("Updating food with id: {}", foodId);
        Food existingFood = foodRepository.findById(foodId)
                .orElseThrow(() -> new NotFoundException("Food with id " + foodId + " not found"));

        foodMapper.partialUpdate(dto, existingFood);

        // Обновление связей
        if (dto.targetSpeciesNames() != null) {
            existingFood.setTargetSpecies(resolveSpecies(dto.targetSpeciesNames()));
        }
        if (dto.targetBreedNames() != null) {
            // Упрощенное разрешение пород
            existingFood.setTargetBreeds(resolveBreeds(dto.targetBreedNames()));
        }
        if (dto.ingredientNames() != null) {
            existingFood.setIngredients(resolveIngredients(dto.ingredientNames()));
        }

        if (dto.minAgeMonths() == null && existingFood.getMinAgeMonths() != null) {
            existingFood.setMinAgeMonths(null);
        }
        if (dto.maxAgeMonths() == null && existingFood.getMaxAgeMonths() != null) {
            existingFood.setMaxAgeMonths(null);
        }

        Food updatedFood = foodRepository.save(existingFood);
        log.info("Food updated successfully: {}", updatedFood.getFoodId());
        return foodMapper.toFoodRecommendationResponseDto(updatedFood);
    }

    @Override
    public void deleteFood(UUID foodId) {
        log.warn("Attempting to delete food with id: {}", foodId);
        if (!foodRepository.existsById(foodId)) {
            throw new NotFoundException("Food with id " + foodId + " not found");
        }

        foodRepository.deleteById(foodId);
        log.info("Food deleted successfully: {}", foodId);
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

    private Set<Ingredient> resolveIngredients(Set<String> names) {
        if (names == null || names.isEmpty()) return Collections.emptySet();
        Set<Ingredient> resolved = new HashSet<>();
        for (String name : names) {
            if (org.springframework.util.StringUtils.hasText(name)) {
                resolved.add(ingredientRepository.findById(name).orElseGet(() -> {
                    log.warn("Ingredient '{}' not found, creating new.", name);
                    Ingredient i = new Ingredient();
                    i.setName(name);
                    return ingredientRepository.save(i);
                }));
            }
        }
        return resolved;
    }

}
