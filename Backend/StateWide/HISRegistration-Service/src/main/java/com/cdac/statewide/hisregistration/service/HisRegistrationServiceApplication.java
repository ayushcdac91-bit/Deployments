package com.cdac.statewide.hisregistration.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients(basePackages = "com.cdac.statewide.hisregistration.service.service")
public class HisRegistrationServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(HisRegistrationServiceApplication.class, args);
	}

}
