package com.josh.emailFunctionality.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.josh.emailFunctionality.entity.EmailTemplateEntity;

public interface EmailTemplateRepository extends JpaRepository<EmailTemplateEntity, Long> {

}
