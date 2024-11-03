package com.virtualpet.vpet.VPet.repository.mysql;

import com.virtualpet.vpet.VPet.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PersonRepository extends JpaRepository<User, Long> {
    User findByUserName(String userName);
}
