package com.josh.emailFunctionality.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.josh.emailFunctionality.entity.EmailStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Data
public class EmailStatusResponseDto {
	
	private EmailStatus status;
	
	@JsonProperty("sent_at")
	private String sentAt;
	
	private String sender;

}
