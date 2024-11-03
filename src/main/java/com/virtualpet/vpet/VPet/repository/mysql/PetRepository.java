package com.virtualpet.vpet.VPet.repository.mysql;

import com.virtualpet.vpet.VPet.model.Companion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;



@Repository
public interface PetRepository extends JpaRepository<Companion, Long> {
}
