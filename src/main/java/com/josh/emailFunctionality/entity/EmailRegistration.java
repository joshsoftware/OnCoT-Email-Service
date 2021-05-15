package com.josh.emailFunctionality.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.josh.emailFunctionality.dto.EmailRegisterReqeustDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "email_registration")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmailRegistration {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;
	
	@Column(name = "email",unique = true)
	private String email;
	
	@Column(name = "password")
	private String password;
	
	
	
	public EmailRegistration(EmailRegisterReqeustDto reqDto)
	{
		this.email = reqDto.getEmail();
		this.password = reqDto.getPassword();
	}
}
