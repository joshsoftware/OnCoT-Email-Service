package com.josh.emailFunctionality.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DriveDetailsRequestDto {

	private String drive;
	private String organization;
	private String start_time;
	private String end_time;
	private List<HrDataRequestDto> hr_contacts;

}
