package com.josh.emailFunctionality.common;

import java.util.HashMap;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Response {
	private String status;
	private String message;
	private String error;
	private Map<String, Object> data = new HashMap<>();
	private String timeStamp;
}
