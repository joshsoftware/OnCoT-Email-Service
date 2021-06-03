package com.josh.emailFunctionality.service;

import java.util.List;

import com.josh.emailFunctionality.dto.EmailRegisterRequestDto;
import com.josh.emailFunctionality.entity.EmailRegistration;

public interface IEmailRegisterService {

	public List<EmailRegistration> getAllEmails();

	public EmailRegistration addEmail(EmailRegisterRequestDto regEmailReqDto);

	public EmailRegistration deleteEmail(long id);
}
