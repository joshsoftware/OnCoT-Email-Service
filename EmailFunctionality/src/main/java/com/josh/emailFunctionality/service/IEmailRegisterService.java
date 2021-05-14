package com.josh.emailFunctionality.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.josh.emailFunctionality.entity.EmailRegistration;

public interface IEmailRegisterService {
	
	public List<EmailRegistration> getAllEmails();

}
