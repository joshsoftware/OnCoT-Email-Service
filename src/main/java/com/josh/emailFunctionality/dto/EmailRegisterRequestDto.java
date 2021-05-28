package com.josh.emailFunctionality.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmailRegisterRequestDto {
	public String email;
	public String password;
}
