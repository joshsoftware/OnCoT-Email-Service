package com.josh.emailFunctionality.service;

import java.util.List;

import javax.mail.Session;


import com.josh.emailFunctionality.dto.EmailRegisterReqeustDto;
import com.josh.emailFunctionality.entity.EmailRegistration;

public interface IEmailRegisterService {
	
	public List<EmailRegistration> getAllEmails();
	public EmailRegistration addEmail(EmailRegisterReqeustDto regEmailReqDto);
	public Session getSession(EmailRegisterReqeustDto regEmailReqDto);	
}
