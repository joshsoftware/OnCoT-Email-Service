package com.josh.emailFunctionality.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmailRegisterResponseDto {
	
	private String email;
	@JsonIgnore
	private String password;
}
