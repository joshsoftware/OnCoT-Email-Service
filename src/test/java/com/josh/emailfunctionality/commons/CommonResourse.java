package com.josh.emailfunctionality.commons;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import com.josh.emailFunctionality.dto.EmailRegisterRequestDto;
import com.josh.emailFunctionality.dto.EmailRequestDto;
import com.josh.emailFunctionality.entity.EmailEntity;
import com.josh.emailFunctionality.entity.EmailRegistration;

public class CommonResourse {

	public static List<EmailRegistration> getAllEmails() {
		List<EmailRegistration> availableEmails = new ArrayList<>();
		availableEmails.add(new EmailRegistration(1l, "test@gmail.com", "test", true));
		return availableEmails;
	}

	public static EmailRegistration getEmailRegistration(EmailRegisterRequestDto emRDto) {
		return new EmailRegistration(emRDto);
	}
	public static EmailRegisterRequestDto getEmailRegistrationRequestDto()
	{
		return new EmailRegisterRequestDto("test@gmail.com", "test");
	}
	
	public static ScheduledThreadPoolExecutor getThreadPoolExecutor()
	{
		return new ScheduledThreadPoolExecutor(1);
	}
	
	public static EmailRequestDto getEmailRequestDto()
	{
		return new EmailRequestDto("test@gmail.com","123");
	}
	public static EmailEntity getEmailEntity()
	{
		return new EmailEntity(getEmailRequestDto());
	}
}
