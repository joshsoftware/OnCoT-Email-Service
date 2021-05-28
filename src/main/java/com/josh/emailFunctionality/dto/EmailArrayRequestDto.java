package com.josh.emailFunctionality.dto;

import java.util.List;

import javax.validation.Valid;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmailArrayRequestDto {
	@ApiModelProperty(required = true)

	@Valid
	public DriveDetailsRequestDto drive_details;
	
	public List<EmailRequestDto> emails;
}
