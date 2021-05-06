package com.josh.emailFunctionality.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.josh.emailFunctionality.dto.EmailRequestDto;
import com.josh.emailFunctionality.entity.EmailEntity;
import com.josh.emailFunctionality.entity.EmailStatus;
import com.josh.emailFunctionality.repository.EmailSendRespository;

@Service
@Transactional
public class EmailSendServiceImpl implements IEmailSendService {

	 @Autowired
	    private JavaMailSender emailSender;
	 
	 @Autowired
	 private EmailSendRespository emailRepository;
	 public static int counter = 0;
	 
	@Override
	public void sendEmail(String to, String subject, String text) throws Exception {
//			try {
			
	        SimpleMailMessage message = new SimpleMailMessage(); 
	        message.setFrom("super.noreply.springboot22@gmail.com");
	        message.setTo(to); 
	        message.setSubject(subject); 
	        message.setText(text);
	        JavaMailSender emailSender2 = emailSender;
			emailSender2.send(message);
			System.out.println("Count :"+ counter + " nddndndndn : " + emailSender2);
			counter++;
			}
//			catch(Exception e) {
//				System.out.println("My Exception stack..........");
//				e.printStackTrace();
//				System.out.println("My Exception stack..........");
//			}
	    
//	}
	
	public EmailEntity saveEmail( EmailRequestDto emailRequestDto) {
		EmailEntity sample = emailRepository.findByToken(emailRequestDto.getToken());
		if(sample == null) {
			EmailEntity email = new EmailEntity(emailRequestDto);
			email.setStatus(EmailStatus.NOTSTARTED);
			EmailEntity email1 = emailRepository.save(email);  
			return email1;
		}
		else {
			return sample;
		}
	
	}
	
	public EmailEntity updateEmail(String token,EmailStatus status) {
		EmailEntity updateEmail = emailRepository.findByToken(token);
		updateEmail.setStatus(status);
		emailRepository.save(updateEmail);
		return updateEmail;
	}


	

}
