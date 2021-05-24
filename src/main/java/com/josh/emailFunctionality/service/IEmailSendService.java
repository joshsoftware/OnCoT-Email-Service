package com.josh.emailFunctionality.service;

import java.util.Map;

import org.springframework.stereotype.Repository;

import com.josh.emailFunctionality.dto.EmailRequestDto;
import com.josh.emailFunctionality.entity.EmailEntity;
import com.josh.emailFunctionality.entity.EmailStatus;

@Repository
public interface IEmailSendService {
	

	public void sendEmail(EmailEntity emailCustom) throws Exception;

	public EmailEntity saveEmail( EmailRequestDto emailRequestDto);
	
	public EmailEntity updateEmail(String token,EmailStatus status,String sender);
	
	public Map<String,EmailStatus> getAllStatusByToken(String[] tkns);


}
