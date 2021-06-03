package com.josh.emailFunctionality;

import java.util.TimeZone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@EnableAsync
@SpringBootApplication
public class EmailFunctionalityApplication {

	public static ConfigurableApplicationContext context;
	public static void main(String[] args) {
		context = SpringApplication.run(EmailFunctionalityApplication.class, args);
		TimeZone.setDefault(TimeZone.getTimeZone("IST"));
		System.out.println(System.getenv("PASSWORD"));
	}
}
