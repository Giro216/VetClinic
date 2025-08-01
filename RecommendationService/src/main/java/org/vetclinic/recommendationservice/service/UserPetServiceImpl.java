package org.vetclinic.recommendationservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.vetclinic.recommendationservice.dto.mapper.UserPetMapper;
import org.vetclinic.recommendationservice.dto.request.UserPetCreateRequestDto;
import org.vetclinic.recommendationservice.dto.request.UserPetUpdateRequestDto;
import org.vetclinic.recommendationservice.dto.response.UserPetResponseDto;
import org.vetclinic.recommendationservice.exception.NotFoundException;
import org.vetclinic.recommendationservice.model.Allergy;
import org.vetclinic.recommendationservice.model.Breed;
import org.vetclinic.recommendationservice.model.Pet;
import org.vetclinic.recommendationservice.model.Species;
import org.vetclinic.recommendationservice.repository.AllergyRepository;
import org.vetclinic.recommendationservice.repository.BreedRepository;
import org.vetclinic.recommendationservice.repository.PetRepository;
import org.vetclinic.recommendationservice.repository.SpeciesRepository;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UserPetServiceImpl implements UserPetService {

    private final PetRepository petRepository;

    private final SpeciesRepository speciesRepository;

    private final BreedRepository breedRepository;

    private final AllergyRepository allergyRepository;

    private final UserPetMapper petMapper;


    @Override
    public UserPetResponseDto createPetForUser(Long userId, UserPetCreateRequestDto dto) {
        Species species = findOrCreateSpecies(dto.speciesName());

        Breed breed = null;
        if (StringUtils.hasText(dto.breedName())) {
            breed = findOrCreateBreed(dto.breedName(), species);
        }

        Set<Allergy> allergies = findOrCreateAllergies(dto.allergyNames());

        Pet pet = new Pet();
        pet.setOwnerId(userId);
        pet.setName(dto.name());
        pet.setBirthDate(dto.birthDate());
        pet.setSpecies(species);
        pet.setBreed(breed);
        pet.setAllergies(allergies);

        return petMapper.toUserPetResponseDto(petRepository.save(pet));
    }

    @Override
    public List<UserPetResponseDto> getPetsForUser(Long userId) {
        log.debug("Fetching pets for user {}", userId);
        List<Pet> pets = petRepository.findByOwnerId(userId);
        log.info("Found {} pets for user {}", pets.size(), userId);
        return pets.stream()
                .map(petMapper::toUserPetResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserPetResponseDto getPetByIdForUser(Long userId, UUID petId) {
        log.debug("Fetching pet {} for user {}", petId, userId);
        Pet pet = petRepository.findByPetIdAndOwnerId(petId, userId)
                .orElseThrow(() -> new NotFoundException("Pet with id " + petId + " not found or not owned by user " + userId));
        return petMapper.toUserPetResponseDto(pet);
    }

    @Override
    public UserPetResponseDto updatePetForUser(Long userId, UUID petId, UserPetUpdateRequestDto dto) {
        log.info("Updating pet {} for user {}", petId, userId);
        Pet existingPet = petRepository.findByPetIdAndOwnerId(petId, userId)
                .orElseThrow(() -> new NotFoundException("Pet with id " + petId + " not found or not owned by user " + userId));

        petMapper.partialUpdate(dto, existingPet);

        if (StringUtils.hasText(dto.speciesName())) {
            Species species = findOrCreateSpecies(dto.speciesName());
            existingPet.setSpecies(species);
            if (!species.getName().equals(existingPet.getSpecies().getName()) && existingPet.getBreed() != null && !StringUtils.hasText(dto.breedName())) {
                log.warn("Species changed for pet {}, removing existing breed '{}' as it might be incompatible.", petId, existingPet.getBreed().getName());
                existingPet.setBreed(null);
            }
        }

        if (dto.breedName() != null) {
            if (StringUtils.hasText(dto.breedName())) {
                Species currentSpecies = existingPet.getSpecies();
                Breed breed = findOrCreateBreed(dto.breedName(), currentSpecies);
                existingPet.setBreed(breed);
            } else {
                existingPet.setBreed(null);
            }
        }


        if (dto.allergyNames() != null) {
            existingPet.setAllergies(findOrCreateAllergies(dto.allergyNames()));
        }

        Pet updatedPet = petRepository.save(existingPet);
        log.info("Pet {} updated successfully for user {}", petId, userId);
        return petMapper.toUserPetResponseDto(updatedPet);
    }

    public Species findOrCreateSpecies(String name) {
        return speciesRepository.findById(name).orElseGet(() -> {
            log.info("Species '{}' not found, creating.", name);
            Species newSpecies = new Species();
            newSpecies.setName(name);
            return speciesRepository.save(newSpecies);
        });
    }

    public Breed findOrCreateBreed(String breedName, Species species) {
        return breedRepository.findByNameAndSpeciesName(breedName, species.getName())
                .orElseGet(() -> {
                    Breed newBreed = new Breed();
                    newBreed.setName(breedName);
                    newBreed.setSpecies(species);
                    return breedRepository.save(newBreed);
                });
    }

    public Set<Allergy> findOrCreateAllergies(Set<String> allergyNames) {
        if (allergyNames == null || allergyNames.isEmpty()) {
            return Collections.emptySet();
        }

        Set<Allergy> resolvedAllergies = new HashSet<>();
        for (String name : allergyNames) {
            if (StringUtils.hasText(name)) {
                Allergy allergy = allergyRepository.findById(name)
                        .orElseGet(() -> {
                            Allergy newAllergy = new Allergy();
                            newAllergy.setName(name);
                            return allergyRepository.save(newAllergy);
                        });
                resolvedAllergies.add(allergy);
            }
        }

        return resolvedAllergies;
    }

}
