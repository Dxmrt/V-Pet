package com.virtualpet.vpet.VPet;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class VirtualCompanionApplication {

	public static void main(String[] args) {
		SpringApplication.run(VirtualCompanionApplication.class, args);
	}

}
