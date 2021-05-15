package com.josh.emailFunctionality.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmailRegisterReqeustDto {
	private String email;
	private String password;
}
