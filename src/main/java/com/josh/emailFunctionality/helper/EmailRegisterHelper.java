package com.josh.emailFunctionality.helper;

import java.util.Properties;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import javax.mail.Authenticator;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;

import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;

import com.josh.emailFunctionality.Exception.AccountNotFoundException;
import com.josh.emailFunctionality.dto.EmailRegisterRequestDto;

@Service
public class EmailRegisterHelper {

	public ScheduledThreadPoolExecutor reinitiateThreadPool(int size)
	{
		int multiplyer = 0;
		if(size == 1)
			multiplyer=2; 
		else
			multiplyer=1;
		
		ScheduledThreadPoolExecutor newScheduledThreadPoolExec = new ScheduledThreadPoolExecutor(size * multiplyer);
		newScheduledThreadPoolExec.setCorePoolSize(size * multiplyer);
		newScheduledThreadPoolExec.setMaximumPoolSize(size * multiplyer);
		return newScheduledThreadPoolExec;
	}
	
	public void testEmailConnection(EmailRegisterRequestDto regEmailReqDto)
	{
		JavaMailSenderImpl mailSenderForTestConnection = new JavaMailSenderImpl();
		mailSenderForTestConnection.setJavaMailProperties(getProperties());
		mailSenderForTestConnection.setSession(getSession(regEmailReqDto));
		try {
			mailSenderForTestConnection.testConnection();
		} catch (MessagingException e) {
			throw new AccountNotFoundException("Invalid Email/Password");
		}
	}
	
	//This method is to get properties that would be used in session
	public Properties getProperties() {
		Properties props = new Properties();
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.socketFactory.port", "465");
		props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.port", "465");
		props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		props.put("mail.smtp.stattls.enabled", "true");
		return props;
	}

	//This method is used to get the session for testing the smtp server connection
	public Session getSession(EmailRegisterRequestDto regEmailReqDto) {
		Session session = Session.getInstance(getProperties(), new Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(regEmailReqDto.getEmail(), regEmailReqDto.getPassword());
			}
		});
		return session;
	}
	
}
