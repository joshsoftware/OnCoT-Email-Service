package com.josh.emailFunctionality.controller;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import javax.mail.Authenticator;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.web.bind.annotation.DeleteMapping;
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
import com.josh.emailFunctionality.Exception.NoEmailAccountsRegisteredException;
import com.josh.emailFunctionality.common.Response;
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
	
	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");	
	public static boolean dailyLimitExceeded = false;
	public static int limitCounter = 0;
	Timestamp timestamp = new Timestamp(System.currentTimeMillis());

	@Autowired
	IEmailSendService emailService;

	@Autowired
	IEmailRegisterService emailRegisterService;

	@Autowired
	ScheduledThreadPoolExecutor scheduledThreadPoolExecutor;

	@Autowired
	ObjectMapper objectMapper;

	@PostMapping("/register")
	public ResponseEntity<?> registerEmailAccount(@RequestBody EmailRegisterReqeustDto regEmailReqDto) {
		JavaMailSenderImpl mailSenderForTestConnection = new JavaMailSenderImpl();
		mailSenderForTestConnection.setJavaMailProperties(emailRegisterService.getProperties());
		mailSenderForTestConnection.setSession(emailRegisterService.getSession(regEmailReqDto));
		try {
			mailSenderForTestConnection.testConnection();
			emailRegisterService.addEmail(regEmailReqDto);	
			Response response = new Response("Success", "Email added in database successfully", "", null, LocalDateTime.now().format(formatter));
			return new ResponseEntity<>(response,HttpStatus.OK);
		} catch (MessagingException e) {
			System.out.println(e.getMessage());
			throw new AccountNotFoundException("Invalid Email/Password");
		}

	}
	
	@GetMapping("/emails")
	public ResponseEntity<?> getRegisteredEmail()
	{
		Response response = new Response("Success", "Registerd Emails", "", new HashMap<>(), LocalDateTime.now().format(formatter));
		response.getData().put("emails", emailRegisterService.getAllEmails());
		return new ResponseEntity<>(response,HttpStatus.OK);
	}
	
	@DeleteMapping("/email/{id}")
	public ResponseEntity<?> deleteEmailAccount(@PathVariable long id) {
			emailRegisterService.deleteEmail(id);
			Response response = new Response("Success", "Email deleted successfully", "", null, LocalDateTime.now().format(formatter));
			return new ResponseEntity<>(response,HttpStatus.OK);
	}
	

	@GetMapping("/findByStatus/{status}")
	public List<EmailEntity> getStatus(@PathVariable EmailStatus status) {
		return emailService.getbyStatus(status);
	}

	static int tokenCounter = 1;

	@PostMapping("/email")
	public ResponseEntity<?> sendEmails(@RequestBody EmailRequestDto emailRequestDto) {
		
		if(emailRegisterService.getAllEmails().size()==0)
			throw new NoEmailAccountsRegisteredException("Please registered at least 1 email account");
		
		System.out.println("Time :" + timestamp);
		emailRequestDto.setToken("Qwerty" + tokenCounter);
		tokenCounter++;
		System.out.println("Pool Size :" + scheduledThreadPoolExecutor.getPoolSize());
		try {
			emailService.sendEmail(emailRequestDto);
		} catch (Exception e) {
			e.printStackTrace();
		}
		Response response = new Response("Success", "Email sending is in progress", "", null, LocalDateTime.now().format(formatter));
		return new ResponseEntity<>(response,HttpStatus.CREATED);
	}

	@GetMapping("/status")
	public ResponseEntity<?> getStatusOfEmails(@RequestBody String tkns)
			throws JsonMappingException, JsonProcessingException {
		String[] tokens = objectMapper.reader().forType(new TypeReference<String[]>() {
		}).readValue(tkns);
		Map<String, EmailStatus> emailStatus = emailService.getAllStatusByToken(tokens);
		Response response = new Response("Success", "Email deleted successfully", "", new HashMap<>(), LocalDateTime.now().format(formatter));
		response.getData().put("Status", emailStatus);
		return new ResponseEntity<>(response,HttpStatus.OK);
	}

}
