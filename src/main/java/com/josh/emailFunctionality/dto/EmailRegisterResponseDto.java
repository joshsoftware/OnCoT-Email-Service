package com.josh.emailFunctionality.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmailRegisterResponseDto {
	public String email;
	@JsonIgnore
	public String password;
}
