package com.virtualpet.vpet.VPet.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "users", uniqueConstraints = {@UniqueConstraint(columnNames = "username")})
public class User {

    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Getter
    @Setter
    @Column(name = "username", nullable = false, unique = true)
    private String userName;

    @Getter
    @Setter
    @Column(name = "password", nullable = false)
    private String userPassword;

    private String capacity;
    @Setter
    @Getter
    @Column(name = "role", nullable = false)
    private String userRole;

    @OneToMany(mappedBy = "ownerId", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Companion> petList = new ArrayList<>();

    public List<Companion> getCompanionList() {
        return this.petList;
    }

    public void setCompanionList(List<Companion> companionList) {
        this.petList = companionList;
    }


}
