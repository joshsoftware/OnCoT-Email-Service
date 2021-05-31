package com.josh.emailFunctionality.dto;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

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
	@NotNull(message = "Emails must not be null")
	@NotEmpty(message = "Empty emails array")
	private List<EmailRequestDto> emails;
}
