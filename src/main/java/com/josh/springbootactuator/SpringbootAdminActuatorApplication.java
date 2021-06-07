package com.josh.springbootactuator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import de.codecentric.boot.admin.server.config.EnableAdminServer;

@SpringBootApplication
@EnableAdminServer
public class SpringbootAdminActuatorApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringbootAdminActuatorApplication.class, args);
	}

}
