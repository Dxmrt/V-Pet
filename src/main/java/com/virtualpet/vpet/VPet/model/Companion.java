package com.virtualpet.vpet.VPet.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Entity
@Table(name = "pets")
public class Companion {

    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long id;

    @Setter
    @Column(name = "name", nullable = false)
    private String name;

    @Setter
    @Column(name = "color", nullable = false)
    private String color;

    @Setter
    @Column(name = "type", nullable = false)
    private String breed;
    @Getter
    @Setter
    @Column(name = "happiness", nullable = false)
    private int happiness;
    @Getter
    @Setter
    @Column(name = "health", nullable = false)
    private int health;
    @Setter
    @Getter
    @Column(name = "cleanliness", nullable = false)
    private int cleanliness;
    @Setter
    @Getter
    @Column(name = "owner_id")
    private Long ownerId;


}
