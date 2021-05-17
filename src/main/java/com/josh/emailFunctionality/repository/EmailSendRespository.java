package com.josh.emailFunctionality.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.josh.emailFunctionality.entity.EmailEntity;
import com.josh.emailFunctionality.entity.EmailStatus;


public interface EmailSendRespository extends JpaRepository<EmailEntity, Long> {
	
	public EmailEntity findByToken(String token);
	
	List<EmailEntity> findByStatus(EmailStatus status);
}
