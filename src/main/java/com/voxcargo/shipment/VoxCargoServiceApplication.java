package com.voxcargo.shipment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class VoxCargoServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(VoxCargoServiceApplication.class, args);
	}

}
