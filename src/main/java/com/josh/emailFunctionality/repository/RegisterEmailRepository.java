package com.josh.emailFunctionality.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.josh.emailFunctionality.entity.EmailRegistration;

@Repository
public interface RegisterEmailRepository extends JpaRepository<EmailRegistration, Long> {

}
