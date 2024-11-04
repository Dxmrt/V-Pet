package com.virtualpet.vpet.VPet.service;

import com.virtualpet.vpet.VPet.model.User;
import com.virtualpet.vpet.VPet.model.UserPrincipal;
import com.virtualpet.vpet.VPet.repository.mysql.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service
public class MyUserDetailsService implements UserDetailsService{

    @Autowired
    private PersonRepository personRepository;

    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        User user = personRepository.findByUserName(userName);
        if (user == null) {
            System.out.println("Usuario no encontrado");
            throw new UsernameNotFoundException("usuario no encontrado");
        }
        return new UserPrincipal(user);
    }
}
