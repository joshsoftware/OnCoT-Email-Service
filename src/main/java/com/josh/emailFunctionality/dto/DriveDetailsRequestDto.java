package com.josh.emailFunctionality.dto;

import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DriveDetailsRequestDto {

	@JsonProperty
	@ApiModelProperty(required = true)
	@NotBlank(message="drive cant be null")
	public String drive;
	@NotNull(message="Org cant be null")
	@ApiModelProperty(required = true)
	public String organization;
	public String start_time;
	public String end_time;
	public List<HrDataRequestDto> hr_contacts;

}
