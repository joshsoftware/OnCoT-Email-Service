package com.josh.emailFunctionality.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.josh.emailFunctionality.dto.EmailRequestDto;
import com.josh.emailFunctionality.dto.EmailResponseDto;
import com.josh.emailFunctionality.entity.EmailEntity;
import com.josh.emailFunctionality.entity.EmailStatus;
import com.josh.emailFunctionality.service.IEmailSendService;

@RestController
@RequestMapping("/api/v1")
public class EmailController {
	
	@Autowired
	IEmailSendService emailService;
	
	@PostMapping("/email")
	public String sendEmails(@RequestBody EmailRequestDto emailRequestDto) {
		List<EmailRequestDto> tempList = new ArrayList<EmailRequestDto>();
		for(int i=1;i<500;i++) {
			String token12 = "qwerty"+i;
			EmailRequestDto em1 = new EmailRequestDto("super.noreply.springboot11@gmail.com",token12);
			tempList.add(em1);
		}
		for(EmailRequestDto emdto : tempList) {
			EmailStatus stat;
			EmailEntity email = emailService.saveEmail(emdto);
			try {
				emailService.sendEmail(emdto.getEmail(), "Subject", "Test text");
				stat = EmailStatus.COMPLETED;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				stat = EmailStatus.FAILED;
			}
			emailService.updateEmail(email.getToken(),stat);
		}
		
		
//		EmailStatus stat;
//		EmailEntity email = emailService.saveEmail(emailRequestDto);
//		try {
//			emailService.sendEmail(emailRequestDto.getEmail(), "Subject", "Test text");
//			stat = EmailStatus.COMPLETED;
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			stat = EmailStatus.FAILED;
//		}
//////		EmailEntity email = emailService.updateEmail(emailRequestDto.getToken());
////		email.setStatus(EmailStatus.COMPLETED);
//		System.out.println("Afetr expection");
//		emailService.updateEmail(email.getToken(),stat);
		return "Ok";
	}

}
