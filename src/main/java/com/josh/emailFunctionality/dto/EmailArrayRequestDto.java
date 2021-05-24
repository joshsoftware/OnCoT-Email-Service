package com.josh.emailFunctionality.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmailArrayRequestDto {
	private List<EmailRequestDto> emails;
}
