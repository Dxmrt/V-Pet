package com.virtualpet.vpet.VPet.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import com.virtualpet.vpet.VPet.model.Companion;
import com.virtualpet.vpet.VPet.model.User;
import com.virtualpet.vpet.VPet.service.PetService;
import com.virtualpet.vpet.VPet.service.PersonService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.util.Map;

public class VirtualPetControllerTest {

    @Mock
    private PetService petService;

    @Mock
    private PersonService personService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private VirtualPetController virtualPetController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreateCompanionEndpoint_Success() {
        // Mock data and setup
        String userName = "testUser";
        Long userId = 1L;
        Map<String, String> payload = Map.of(
                "petName", "Gambita",
                "petColor", "yellow",
                "petBreed", "gamba"
        );

        Companion companion = new Companion();
        when(authentication.getName()).thenReturn(userName);
        when(personService.getUserId(userName)).thenReturn(userId);
        when(petService.createCompanion(userId, "Gambita", "yellow", "gamba")).thenReturn(new User());

        // Perform test
        ResponseEntity<User> response = virtualPetController.createCompanion(payload, authentication);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
    }
}
