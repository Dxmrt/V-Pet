package com.virtualpet.vpet.VPet.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.virtualpet.vpet.VPet.exception.UserNotFoundException;
import com.virtualpet.vpet.VPet.model.Companion;
import com.virtualpet.vpet.VPet.model.User;
import com.virtualpet.vpet.VPet.repository.mysql.PersonRepository;
import com.virtualpet.vpet.VPet.repository.mysql.PetRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class PetServiceTest {

    @Mock
    private PersonRepository personRepository;

    @Mock
    private PetRepository petRepository;

    @InjectMocks
    private PetService petService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreateCompanion_Success() {
        // Setup mock data and expectations
        Long userId = 1L;
        String petName = "Gambita";
        String petColor = "yellow";
        String petType = "gamba";
        User user = new User();
        when(personRepository.findById(userId)).thenReturn(java.util.Optional.of(user));

        User result = petService.createCompanion(userId, petName, petColor, petType);

        assertNotNull(result);
        verify(petRepository, times(1)).save(any(Companion.class));
    }

    @Test
    public void testCreateCompanion_UserNotFound() {
        Long userId = 999L;
        when(personRepository.findById(userId)).thenReturn(java.util.Optional.empty());

        assertThrows(UserNotFoundException.class, () -> {
            petService.createCompanion(userId, "Gambita", "yellow", "gamba");
        });
    }
}
