package com.josh.emailFunctionality.dto;

import javax.validation.constraints.NotBlank;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmailRegisterRequestDto {
	
	@NotBlank(message="Email cannot be blank or null")
	@ApiModelProperty(required = true)
	private String email;
	
	@NotBlank(message="Password cannot be blank or null")
	@ApiModelProperty(required = true)
	private String password;
}
