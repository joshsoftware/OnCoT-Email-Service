package com.josh.emailFunctionality.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.josh.emailFunctionality.entity.EmailRegistration;

@Repository
public interface RegisterEmailRepository extends JpaRepository<EmailRegistration, Long> {
	
	public EmailRegistration findByEmail(String email);
	@Query("select e from EmailRegistration e where e.isAvailable=true")
	public List<EmailRegistration> findAllIsAvailable();
}
