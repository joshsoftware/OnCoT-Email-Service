package com.josh.emailFunctionality.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.josh.emailFunctionality.entity.EmailEntity;

public interface EmailSendRespository extends JpaRepository<EmailEntity, Long> {
	
	public EmailEntity findByToken(String token);

}
