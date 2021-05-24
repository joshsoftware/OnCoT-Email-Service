package com.josh.emailFunctionality.service;

import java.util.List;
import java.util.Properties;

import javax.mail.Session;

import com.josh.emailFunctionality.dto.EmailRegisterRequestDto;
import com.josh.emailFunctionality.entity.EmailRegistration;

public interface IEmailRegisterService {

	public List<EmailRegistration> getAllEmails();

	public EmailRegistration addEmail(EmailRegisterRequestDto regEmailReqDto);

	public Session getSession(EmailRegisterRequestDto regEmailReqDto);

	public Properties getProperties();

	public EmailRegistration deleteEmail(long id);
}
