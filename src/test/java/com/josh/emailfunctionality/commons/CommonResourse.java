package com.josh.emailfunctionality.commons;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import com.josh.emailFunctionality.dto.DriveDetailsRequestDto;
import com.josh.emailFunctionality.dto.EmailArrayRequestDto;
import com.josh.emailFunctionality.dto.EmailRegisterRequestDto;
import com.josh.emailFunctionality.dto.EmailRequestDto;
import com.josh.emailFunctionality.dto.HrDataRequestDto;
import com.josh.emailFunctionality.entity.EmailEntity;
import com.josh.emailFunctionality.entity.EmailRegistration;

public class CommonResourse {

	private static EmailRegistration emailRegistration = new EmailRegistration(1L, "test@gmail.com", "test", true);
	private static EmailRegisterRequestDto emailRegisterRequestDto = new EmailRegisterRequestDto("test@gmail.com", "test");
	private static ScheduledThreadPoolExecutor scheduledThreadPoolExecutor= new ScheduledThreadPoolExecutor(1);
	private static EmailRequestDto emailRequestDto = new EmailRequestDto("test@gmail.com","123");
	private static EmailEntity emailEntity = new EmailEntity(emailRequestDto);
	private static List<EmailRegistration> availableEmails;
	private static List<HrDataRequestDto> hrData = new ArrayList<HrDataRequestDto>();
	private static DriveDetailsRequestDto driveDetailsRequestDto = new DriveDetailsRequestDto("test","test_sample","2021-11-05 22:10:22","2021-12-24 10:03:22", hrData);
	private static List<EmailRequestDto> emailRequestDtos = new ArrayList<>();
	
	public static EmailArrayRequestDto getEmailArrayRequestDto()
	{
		HrDataRequestDto hrReq = new HrDataRequestDto("test", "test");
		hrData.add(hrReq);
		emailRequestDtos.add(emailRequestDto);
		return new EmailArrayRequestDto(driveDetailsRequestDto, emailRequestDtos);
	}
	
	public static List<EmailRegistration> getAllEmails() {
		availableEmails = new ArrayList<>();
		availableEmails.add(emailRegistration);
		return availableEmails;
	}

	public static EmailRegistration getEmailRegistration() {
		return emailRegistration;
	}
	public static EmailRegistration getNewEmailRegistration() {
		return new EmailRegistration(emailRegisterRequestDto);
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
