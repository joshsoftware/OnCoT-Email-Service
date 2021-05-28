package com.josh.emailFunctionality.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EmailResponseDto {

	public String message;
	
	public Integer successStatus;

}
