package com.josh.emailFunctionality.controller;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import javax.mail.MessagingException;
import javax.mail.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.josh.emailFunctionality.Exception.AccountNotFoundException;
import com.josh.emailFunctionality.configuration.ThreadPoolTaskSchedulerConfig;
import com.josh.emailFunctionality.dto.EmailRegisterReqeustDto;
import com.josh.emailFunctionality.dto.EmailRequestDto;
import com.josh.emailFunctionality.entity.EmailEntity;
import com.josh.emailFunctionality.entity.EmailRegistration;
import com.josh.emailFunctionality.entity.EmailStatus;
import com.josh.emailFunctionality.service.IEmailRegisterService;
import com.josh.emailFunctionality.service.IEmailSendService;

@RestController
@RequestMapping("/api/v1")
public class EmailController {
	public static boolean dailyLimitExceeded = false;

	public static int limitCounter = 0;
	Timestamp timestamp = new Timestamp(System.currentTimeMillis());
    
	@Autowired
	JavaMailSenderImpl mailSenderForTestConnection;
	
	@Autowired
	IEmailSendService emailService;
	
	@Autowired
	IEmailRegisterService emailRegisterService;


	@Autowired
	ScheduledThreadPoolExecutor scheduledThreadPoolExecutor;
	
	@Autowired
	ObjectMapper objectMapper;

	@PostMapping("/register")
	public ResponseEntity<?> registerEmailAccount(@RequestBody EmailRegisterReqeustDto regEmailReqDto)
	{
		
		Session session = emailRegisterService.getSession(regEmailReqDto);
		mailSenderForTestConnection.setSession(session);
		try {
			mailSenderForTestConnection.testConnection();
			EmailRegistration emailRegistration = emailRegisterService.addEmail(regEmailReqDto);
			List<EmailRegistration> emails = emailRegisterService.getAllEmails();
			System.out.println("Size of emails is " + emails.size());
			ScheduledThreadPoolExecutor schThP = new ScheduledThreadPoolExecutor(emails.size()*2);
			schThP.setCorePoolSize(emails.size()*2);
			//schThP.setMaximumPoolSize(emails.size()*2);
			
			new ThreadPoolTaskSchedulerConfig().reintialiseBean(schThP);
			
			return new ResponseEntity<>(emailRegistration,HttpStatus.OK);
		} catch (MessagingException e) {
			System.out.println(e.getMessage());
			throw new AccountNotFoundException("Invalid Email/Password");
		}
		
	}


	@GetMapping("/findByStatus/{status}")
	public List<EmailEntity> getStatus(@PathVariable EmailStatus status) {
		return emailService.getbyStatus(status);
	}


	static int tokenCounter = 1;

	@PostMapping("/email")
	public ResponseEntity<?> sendEmails(@RequestBody EmailRequestDto emailRequestDto) {
		System.out.println("Time :" + timestamp);
		emailRequestDto.setToken("Qwerty" + tokenCounter);
		tokenCounter++;
		System.out.println("Pool Size :" + scheduledThreadPoolExecutor.getPoolSize());
		try {
			emailService.sendEmail(emailRequestDto);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ResponseEntity.ok().body("Ok");
	}
	
	@GetMapping("/status")
	public ResponseEntity<?> getStatusOfEmails(@RequestBody String tkns) throws JsonMappingException, JsonProcessingException
	{
		
		String[] tokens = objectMapper.reader().forType(new TypeReference<String[]>() {}).readValue(tkns);
		Map<String,EmailStatus> emailEntities = emailService.getAllStatusByToken(tokens);
		return new ResponseEntity<>(emailEntities, HttpStatus.OK);
	}

}
