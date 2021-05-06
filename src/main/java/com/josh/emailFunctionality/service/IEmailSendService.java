package com.josh.emailFunctionality.service;

import org.springframework.stereotype.Repository;

import com.josh.emailFunctionality.dto.EmailRequestDto;
import com.josh.emailFunctionality.entity.EmailEntity;
import com.josh.emailFunctionality.entity.EmailStatus;

@Repository
public interface IEmailSendService {
	

	public void sendEmail(String to, String subject, String text) throws Exception;

	public EmailEntity saveEmail( EmailRequestDto emailRequestDto);
	
	public EmailEntity updateEmail(String token,EmailStatus status);

}
