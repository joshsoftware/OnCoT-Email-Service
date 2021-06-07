package com.josh.emailFunctionality.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.josh.emailFunctionality.dto.EmailRequestDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "email_status")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmailEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@Column(name = "email")
	private String email;

	@Column(name = "token")
	private String token;

	@Column(name = "status")
	private EmailStatus status;

	@Column(name = "sent_by")
	private String sender;
	
	@Column(name = "sent_at")
	@Temporal(TemporalType.TIMESTAMP)
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private Date timeStamp;

	public EmailEntity(EmailRequestDto emailRequestDto) {
		super();
		this.email = emailRequestDto.getEmail();
		this.token = emailRequestDto.getToken();
	}

}
