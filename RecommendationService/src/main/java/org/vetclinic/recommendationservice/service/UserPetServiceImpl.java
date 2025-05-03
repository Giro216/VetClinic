package org.vetclinic.recommendationservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.vetclinic.recommendationservice.dto.mapper.UserPetMapper;
import org.vetclinic.recommendationservice.dto.request.UserPetCreateRequestDto;
import org.vetclinic.recommendationservice.dto.response.UserPetResponseDto;
import org.vetclinic.recommendationservice.model.Allergy;
import org.vetclinic.recommendationservice.model.Breed;
import org.vetclinic.recommendationservice.model.Pet;
import org.vetclinic.recommendationservice.model.Species;
import org.vetclinic.recommendationservice.repository.AllergyRepository;
import org.vetclinic.recommendationservice.repository.BreedRepository;
import org.vetclinic.recommendationservice.repository.PetRepository;
import org.vetclinic.recommendationservice.repository.SpeciesRepository;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

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

    private Species findOrCreateSpecies(String name) {
        return speciesRepository.findById(name).orElseGet(() -> {
            Species newSpecies = new Species();
            newSpecies.setName(name);
            return speciesRepository.save(newSpecies);
        });
    }

    private Breed findOrCreateBreed(String breedName, Species species) {
        return breedRepository.findByNameAndSpeciesName(breedName, species.getName())
                .orElseGet(() -> {
                    Breed newBreed = new Breed();
                    newBreed.setName(breedName);
                    newBreed.setSpecies(species);
                    return breedRepository.save(newBreed);
                });
    }

    private Set<Allergy> findOrCreateAllergies(Set<String> allergyNames) {
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
