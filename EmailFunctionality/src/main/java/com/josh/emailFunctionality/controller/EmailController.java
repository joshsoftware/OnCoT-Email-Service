package com.josh.emailFunctionality.controller;

import java.sql.Timestamp;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.josh.emailFunctionality.dto.EmailRequestDto;
import com.josh.emailFunctionality.entity.EmailEntity;
import com.josh.emailFunctionality.entity.EmailStatus;
import com.josh.emailFunctionality.service.IEmailSendService;

@RestController
@RequestMapping("/api/v1")
public class EmailController {
	public static boolean dailyLimitExceeded = false;

	public static int limitCounter = 0;
	Timestamp timestamp = new Timestamp(System.currentTimeMillis());
    
	
	@Autowired
	IEmailSendService emailService;


	@Autowired
	ScheduledThreadPoolExecutor scheduledThreadPoolExecutor;



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

}
