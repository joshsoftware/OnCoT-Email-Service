package com.josh.emailFunctionality.dto;

import javax.validation.constraints.NotBlank;

import io.swagger.annotations.ApiModelProperty;

public class SampleDto {
	
	@ApiModelProperty(required = true)
	@NotBlank(message="hxhxhx")
	public String name;
	
	public int age;

}
