package com.virtualpet.vpet.VPet.service;


import com.virtualpet.vpet.VPet.exception.UserNotFoundException;
import com.virtualpet.vpet.VPet.model.Companion;

import com.virtualpet.vpet.VPet.model.User;
import com.virtualpet.vpet.VPet.repository.mysql.PersonRepository;
import com.virtualpet.vpet.VPet.repository.mysql.PetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PersonService {

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private PetRepository petRepository;

    @Autowired
    private JWTService jwtService;

    @Autowired
    AuthenticationManager authManager;

    @Autowired
    private BCryptPasswordEncoder encoder;

    public String verify(String userName, String userPassword) {
        User user = personRepository.findByUserName(userName);
        Authentication authentication = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(userName, userPassword));

        if (authentication.isAuthenticated()) {
            return jwtService.generateToken(userName, user.getUserRole());
        } else {
            return "fail";
        }
    }

    public User createUser(String name, String password, String role) {
        // Normalizar el nombre de usuario a minúsculas para evitar problemas de duplicados
        name = name.toLowerCase();

        // Verificar si el usuario ya existe
        if (personRepository.findByUserName(name) != null) {
            throw new IllegalStateException("User already exists with username: " + name);
        }

        // Crear nuevo usuario y establecer su información
        User user = new User();
        userInfo(user, name, password, role);
        return personRepository.save(user);
    }

    public void userInfo(User user, String name, String password, String role) {
        user.setUserName(name);
        user.setUserPassword(encoder.encode(password));
        user.setUserRole(role);
        user.setCompanionList(new ArrayList<>());
    }

    public List<Companion> getUserCompanions(Long userId) {
        User user = findUser(userId);
        return user.getCompanionList();
    }

    public List<Companion> getAllCompanions() {
        return petRepository.findAll();
    }

    public boolean isAdmin(Long userId) {
        User user = findUser(userId);
        return user.getUserRole().equals("ADMIN");
    }

    public void deleteUser(Long userId) {
        User user = personRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));
        personRepository.delete(user);
    }


    public User findUser(Long userId) {
        return personRepository.findAll()
                .stream()
                .filter(user -> user.getId().equals(userId))
                .findFirst()
                .orElse(null);
    }

    public Long getUserId(String userName) {
        User user = personRepository.findByUserName(userName);
        return user.getId();
    }

    public String getRole(String userName) {
        User user = personRepository.findByUserName(userName);
        return user.getUserRole();
    }



    public boolean isAdmin(String userName) {
        return getRole(userName).equals("ADMIN");
    }
}
