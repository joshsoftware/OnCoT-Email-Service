package com.josh.emailFunctionality.dto;

import java.util.List;

import javax.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DriveDetailsRequestDto {

	@ApiModelProperty(required = true)
	@NotBlank(message="Drive name cannot be blank or null")
	@JsonProperty("drive_name")
	private String drive;
	
	@NotBlank(message="Organization name cannot be blank or null")
	@ApiModelProperty(required = true)
	@JsonProperty("organization_name")
	private String organization;
	
	private String start_time;
	
	private String end_time;
	
	private List<HrDataRequestDto> hr_contacts;

}
