package com.virtualpet.vpet.VPet.service;

import com.virtualpet.vpet.VPet.exception.PetNotFoundException;
import com.virtualpet.vpet.VPet.exception.UnauthorizedActionException;
import com.virtualpet.vpet.VPet.exception.UserNotFoundException;
import com.virtualpet.vpet.VPet.model.Companion;
import com.virtualpet.vpet.VPet.model.User;
import com.virtualpet.vpet.VPet.repository.mysql.PersonRepository;
import com.virtualpet.vpet.VPet.repository.mysql.PetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class PetService {

    @Autowired
    private final PersonRepository personRepository;
    @Autowired
    private final PetRepository petRepository;

    public User createCompanion(Long userId, String petName, String petColor, String petBreed) {
        User user = findUserOrThrow(userId);

        if (user.getCompanionList().size() >= 3) {
            return null;  // No necesita excepción, ya que la lógica lo permite
        }

        Companion companion = new Companion();
        petInfo(companion, petName, petColor, petBreed);
        companion.setOwnerId(userId);
        user.getCompanionList().add(companion);

        return personRepository.save(user);
    }

    private User findUserOrThrow(Long userId) {
        return personRepository.findAll()
                .stream()
                .filter(user -> user.getId().equals(userId))
                .findFirst()
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));
    }

    private void petInfo(Companion companion, String petName, String petColor, String petBreed) {
        companion.setName(petName);
        companion.setColor(petColor);
        companion.setBreed(petBreed);
        companion.setHappiness(100);
        companion.setHealth(100);
        companion.setCleanliness(100);
    }

    @Scheduled(fixedRate = 60000) // 60000 ms = 1 minuto
    public void decreaseStats() {
        List<Companion> allPets = petRepository.findAll();
        allPets.forEach(this::decreasePetStats);
        petRepository.saveAll(allPets);
    }

    private void decreasePetStats(Companion pet) {
        pet.setCleanliness(Math.max(0, pet.getCleanliness() - 5));
        pet.setHappiness(Math.max(0, pet.getHappiness() - 5));
        pet.setHealth(Math.max(0, pet.getHealth() - 5));
    }

    public void deleteCompanion(Long petId, Long userId, boolean isAdmin) {
        Companion pet = findCompanionOrThrow(petId);

        if (!isAdmin && !isOwner(userId, petId)) {
            throw new UnauthorizedActionException("User not authorized to delete this pet.");
        }

        petRepository.delete(pet);
    }

    private Companion findCompanionOrThrow(Long petId) {
        return petRepository.findById(petId)
                .orElseThrow(() -> new PetNotFoundException("Pet not found with id: " + petId));
    }

    public Companion updateCompanion(Long petId, String update, String change) {
        Companion pet = findCompanionOrThrow(petId);
        update(pet, update, change);
        return petRepository.save(pet);
    }

    private void update(Companion pet, String update, String change) {
        switch (update) {
            case "change_name" -> pet.setName(change);
            case "change_color" -> pet.setColor(change);
            case "change_breed" -> pet.setBreed(change);
        }
    }

    public Companion petAction(Long petId, String action) {
        Companion pet = findCompanionOrThrow(petId);
        action(pet, action);
        return petRepository.save(pet);
    }

    private void action(Companion pet, String action) {
        switch (action) {
            case "play" -> {
                pet.setHappiness(pet.getHappiness() + 10);
                pet.setHealth(pet.getHealth() - 20);
            }
            case "feed" -> {
                pet.setHappiness(pet.getHappiness() + 10);
                pet.setHealth(pet.getHealth() + 10);
            }
            case "clean" -> {
                pet.setCleanliness(Math.min(100, pet.getCleanliness() + 15));
                pet.setHealth(pet.getHealth() - 10);
            }
        }
    }

    public boolean isOwner(Long userId, Long petId) {
        Companion pet = findCompanionOrThrow(petId);
        return Objects.equals(userId, pet.getOwnerId());
    }
}
