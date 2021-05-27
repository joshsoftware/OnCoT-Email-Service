package com.josh.emailfunctionality.commons;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import com.josh.emailFunctionality.dto.EmailRegisterRequestDto;
import com.josh.emailFunctionality.dto.EmailRequestDto;
import com.josh.emailFunctionality.entity.EmailEntity;
import com.josh.emailFunctionality.entity.EmailRegistration;

public class CommonResourse {

	private static EmailRegistration emailRegistration = new EmailRegistration(1L, "test@gmail.com", "test", true);
	private static EmailRegisterRequestDto emailRegisterRequestDto = new EmailRegisterRequestDto("test@gmail.com", "test");
	private static ScheduledThreadPoolExecutor scheduledThreadPoolExecutor= new ScheduledThreadPoolExecutor(1);
	private static EmailRequestDto emailRequestDto = new EmailRequestDto("test@gmail.com","123");
	private static EmailEntity emailEntity = new EmailEntity(emailRequestDto);
	private static List<EmailRegistration> availableEmails = new ArrayList<>();
	
	public static List<EmailRegistration> getAllEmails() {
		availableEmails.add(emailRegistration);
		return availableEmails;
	}

	public static EmailRegistration getEmailRegistration() {
		return emailRegistration;
	}
	public static EmailRegisterRequestDto getEmailRegistrationRequestDto()
	{
		return emailRegisterRequestDto;
	}
	
	public static ScheduledThreadPoolExecutor getThreadPoolExecutor()
	{
		return scheduledThreadPoolExecutor;
	}
	
	public static EmailRequestDto getEmailRequestDto()
	{
		return emailRequestDto;
	}
	public static EmailEntity getEmailEntity()
	{
		return emailEntity;
	}
}
