package com.josh.emailFunctionality.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.josh.emailFunctionality.entity.EmailRegistration;
import com.josh.emailFunctionality.repository.RegisterEmailRepository;

@Service
@Transactional
public class EmailRegisterServiceImpl implements IEmailRegisterService {
	
	@Autowired
	RegisterEmailRepository registerEmailRepository;

	@Override
	public List<EmailRegistration> getAllEmails() {
		List<EmailRegistration> availableEmails = registerEmailRepository.findAll();
		return availableEmails;
	}

}
