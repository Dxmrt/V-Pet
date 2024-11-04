package com.virtualpet.vpet.VPet.controller;

import com.virtualpet.vpet.VPet.exception.AccessDeniedException;
import com.virtualpet.vpet.VPet.model.Companion;
import com.virtualpet.vpet.VPet.model.User;
import com.virtualpet.vpet.VPet.service.PetService;
import com.virtualpet.vpet.VPet.service.PersonService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/virtualpet")
@Tag(name = "Companion Controller", description = "Controller for mapping Virtual Companion App")
public class VirtualPetController {

    @Autowired
    private PersonService personService;
    @Autowired
    private PetService petService;

    @PostMapping("/login")
    @Operation(summary = "Login a user", description = "Login a user, login form")
    public String login(@RequestBody Map<String, String> payload) {
        String userName = payload.get("userName");
        String userPassword = payload.get("userPassword");

        return personService.verify(userName, userPassword);
    }

    @PostMapping("/register")
    @Operation(summary = "Register a new user", description = "Register a new user, register form")
    public ResponseEntity<Map<String, String>> register(@RequestBody Map<String, String> payload) {
        String userName = payload.get("userName");
        String userPassword = payload.get("userPassword");
        String userRole = payload.get("userRole");

        User newUser = personService.createUser(userName, userPassword, userRole);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "Usuario registrado con exito."));
    }



    @PostMapping("/create")
    @Operation(summary = "Create a virtual companion", description = "Create a companion")
    public ResponseEntity<User> createCompanion(@RequestBody Map<String, String> payload, Authentication authentication) {
        String userName = authentication.getName();
        Long userId = personService.getUserId(userName);

        String petName = payload.get("petName");
        String petColor = payload.get("petColor");
        String petBreed = payload.get("petBreed");

        User newOwner = petService.createCompanion(userId, petName, petColor, petBreed);

        return (newOwner != null)
                ? new ResponseEntity<>(newOwner, HttpStatus.CREATED)
                : new ResponseEntity<>(null, HttpStatus.FORBIDDEN);
    }



    @GetMapping("/user/pets")
    @Operation(summary = "View companions", description = "Allows to see user's companion")
    public ResponseEntity<List<Companion>> getUserCompanions(Authentication authentication){
        String userName = authentication.getName();
        Long userId = personService.getUserId(userName);

        List<Companion> petList = personService.getUserCompanions(userId);
        return new ResponseEntity<>(petList, HttpStatus.OK);
    }


    @DeleteMapping("/user/delete/{userId}")
    @Operation(summary = "Delete a user", description = "Delete a user from the db")
    public ResponseEntity<Void> deleteUser(@PathVariable Long userId) {
        personService.deleteUser(userId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


    @DeleteMapping("/pet/delete/{petId}")
    @Operation(summary = "Delete a user's companion", description = "Delete a companion from the db")
    public ResponseEntity<Void> deleteCompanion(@PathVariable Long petId, Authentication authentication) {
        String userName = authentication.getName();
        Long userId = personService.getUserId(userName);
        boolean isAdmin = personService.isAdmin(userName);

        petService.deleteCompanion(petId, userId, isAdmin);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


    @PutMapping("pet/update/{petId}")
    @Operation(summary = "Update a companion", description = "Update companion's information")
    public ResponseEntity<Companion> updateCompanion(@PathVariable Long petId,
                                                     @RequestBody Map<String, String> payload,
                                                     Authentication authentication) {

        String userName = authentication.getName();
        Long userId = personService.getUserId(userName);
        boolean isAdmin = personService.isAdmin(userName);
        String update = payload.get("update");
        String change = payload.get("change");

        if (isAdmin || petService.isOwner(userId, petId)){
            Companion pet = petService.updateCompanion(petId, update, change);
            return new ResponseEntity<>(pet, HttpStatus.OK);
        } else {
            throw new AccessDeniedException("Este usuario no tiene permiso para actualizar esta mascota.");
        }
    }


    @PutMapping("pet/action/{petId}")
    @Operation(summary = "Interact with a companion", description = "Interact with user's companion")
    public ResponseEntity<Companion> petAction(@PathVariable Long petId,
                                               @RequestBody Map<String, String> payload,
                                               Authentication authentication) {

        String userName = authentication.getName();
        Long userId = personService.getUserId(userName);
        boolean isAdmin = personService.isAdmin(userName);
        String action = payload.get("action");

        if (isAdmin || petService.isOwner(userId, petId)) {
            Companion pet = petService.petAction(petId, action);
            return new ResponseEntity<>(pet, HttpStatus.OK);
        } else {
            throw new AccessDeniedException("Este usuario no tiene permiso para interactuar con esta mascota.");
        }
    }


    @GetMapping("/admin/pets")
    @Operation(summary = "View all companions in the db",
            description = "Allows ADMIN to see all companions in the db")
    public ResponseEntity<List<Companion>> getAllCompanions() {
        List<Companion> allCompanions = personService.getAllCompanions();
        return new ResponseEntity<>(allCompanions, HttpStatus.OK);
    }

    @GetMapping("/role")
    @Operation(summary = "Get user's role", description = "Get user's role request")
    public ResponseEntity<Map<String, String>> getUserRole(Authentication authentication) {
        String userName = authentication.getName();
        String userRole = personService.getRole(userName);

        return new ResponseEntity<>(Map.of("role", userRole), HttpStatus.OK);
    }

}
