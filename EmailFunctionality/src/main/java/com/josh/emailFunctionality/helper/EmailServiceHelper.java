package com.josh.emailFunctionality.helper;

import java.util.List;

import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.SimpleEmail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.josh.emailFunctionality.entity.EmailRegistration;
import com.josh.emailFunctionality.service.IEmailRegisterService;

@Service
public class EmailServiceHelper {
	
	 @Autowired
	 private IEmailRegisterService emailRegisterService;
	 
	 public static int counter = 0;
	 
	 public static int sendCheckCounter = 0;
	 
	public void sendEmailHelper(String to, String subject, String text) throws Exception{
		List<EmailRegistration> sendEm = emailRegisterService.getAllEmails();
		Email email = new SimpleEmail();
		email.setHostName("smtp.googlemail.com");
		email.setSmtpPort(587);
	//	email.setAuthenticator(new DefaultAuthenticator("super.noreply.springboot11@gmail.com", "Chinmay@Super@2021"));
		email.setAuthenticator(new DefaultAuthenticator(sendEm.get(sendCheckCounter).getEmail(), sendEm.get(sendCheckCounter).getPassword()));
		email.setSSLOnConnect(true);
		email.setFrom(sendEm.get(sendCheckCounter).getEmail());
		email.setSubject("TestMailAfter size change");
		email.setMsg("This is a test mail ... :-) sent from " + sendEm.get(sendCheckCounter).getEmail());
		email.addTo(to);
		email.send();
		sendCheckCounter++;
		if(sendCheckCounter > sendEm.size()-1) {
			sendCheckCounter = 0;
		}
		System.out.println("Count : Email Sent : "+counter);
		counter++;
	}

}
