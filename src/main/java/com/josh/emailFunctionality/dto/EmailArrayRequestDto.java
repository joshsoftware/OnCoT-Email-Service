package com.josh.emailFunctionality.dto;

import java.util.List;

import javax.validation.Valid;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmailArrayRequestDto {
	

	@Valid
	private DriveDetailsRequestDto drive_details;
	
	@Valid
	private List<EmailRequestDto> emails;
}
