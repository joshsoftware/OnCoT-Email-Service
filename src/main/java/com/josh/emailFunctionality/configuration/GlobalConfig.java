package com.josh.emailFunctionality.configuration;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;


@Configuration
public class GlobalConfig {
	
	@Bean
	public ObjectMapper objectMapper()
	{
		return new ObjectMapper();		
	}
	
}
