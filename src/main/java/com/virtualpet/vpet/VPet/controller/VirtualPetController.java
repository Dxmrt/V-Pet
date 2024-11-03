package com.virtualpet.vpet.VPet.controller;

import com.virtualpet.vpet.VPet.model.Companion;
import com.virtualpet.vpet.VPet.model.User;
import com.virtualpet.vpet.VPet.service.PetService;
import com.virtualpet.vpet.VPet.service.PersonService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
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
    @Operation(summary = "Login a user", description = "Login a user in the web")
    public String login(@RequestBody Map<String, String> payload) {
        String userName = payload.get("userName");
        String userPassword = payload.get("userPassword");

        return personService.verify(userName, userPassword);
    }

    @PostMapping("/register")
    @Operation(summary = "Register a new user", description = "Register a new user to use the web")
    public ResponseEntity<Map<String, String>> register(@RequestBody Map<String, String> payload) {
        String userName = payload.get("userName");
        String userPassword = payload.get("userPassword");
        String userRole = payload.get("userRole");

        User newUser = personService.createUser(userName, userPassword, userRole);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "User registered successfully"));
    }



    @PostMapping("/create")
    @Operation(summary = "Create a virtual pet", description = "Create a pet for a user")
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
    @Operation(summary = "View user's pets", description = "Allows to see the user's pets")
    public ResponseEntity<List<Companion>> getUserCompanions(Authentication authentication){
        String userName = authentication.getName();
        Long userId = personService.getUserId(userName);

        List<Companion> petList = personService.getUserCompanions(userId);
        return new ResponseEntity<>(petList, HttpStatus.OK);
    }

    @GetMapping("/admin/pets")
    @Operation(summary = "View all pets in the Database",
            description = "Allows the ADMIN to see all the pets in the Database")
    public ResponseEntity<List<Companion>> getAllCompanions() {
        List<Companion> allCompanions = personService.getAllCompanions();
        return new ResponseEntity<>(allCompanions, HttpStatus.OK);
    }

    @DeleteMapping("/user/delete/{userId}")
    @Operation(summary = "Delete a user", description = "Delete a user from the Database")
    public ResponseEntity<Void> deleteUser(@PathVariable Long userId) {
        personService.deleteUser(userId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


    @DeleteMapping("/pet/delete/{petId}")
    @Operation(summary = "Delete a user's pet", description = "Delete a pet from the Database")
    public ResponseEntity<Void> deleteCompanion(@PathVariable Long petId, Authentication authentication) {
        String userName = authentication.getName();
        Long userId = personService.getUserId(userName);
        boolean isAdmin = personService.isAdmin(userName);

        petService.deleteCompanion(petId, userId, isAdmin);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


    @PutMapping("pet/update/{petId}")
    @Operation(summary = "Update a pet", description = "Update the pet's info")
    public ResponseEntity<Companion> updateCompanion(@PathVariable Long petId,
                                         @RequestBody Map<String, String> payload,
                                         Authentication authentication) {
        try {
            String userName = authentication.getName();
            Long userId = personService.getUserId(userName);
            boolean isAdmin = personService.isAdmin(userName);
            String update = payload.get("update");
            String change = payload.get("change");

            if (isAdmin || petService.isOwner(userId, petId)){
                Companion pet = petService.updateCompanion(petId,update,change);
                return new ResponseEntity<>(pet, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("pet/action/{petId}")
    @Operation(summary = "Interact with a pet", description = "Interact with a user's pet")
    public ResponseEntity<Companion> petAction(@PathVariable Long petId,
                                         @RequestBody Map<String, String> payload,
                                         Authentication authentication) {
        try {
            String userName = authentication.getName();
            Long userId = personService.getUserId(userName);
            boolean isAdmin = personService.isAdmin(userName);
            String action = payload.get("action");

            if (isAdmin || petService.isOwner(userId, petId)){
                Companion pet = petService.petAction(petId,action);
                return new ResponseEntity<>(pet, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/role")
    @Operation(summary = "get the user's role", description = "Get the user's role for other request")
    public ResponseEntity<Map<String, String>> getUserRole(Authentication authentication) {
        String userName = authentication.getName();
        String userRole = personService.getRole(userName);

        return new ResponseEntity<>(Map.of("role", userRole), HttpStatus.OK);
    }

}
