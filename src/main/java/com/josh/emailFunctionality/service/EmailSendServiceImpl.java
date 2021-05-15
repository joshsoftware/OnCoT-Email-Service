package com.josh.emailFunctionality.service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.josh.emailFunctionality.dto.EmailRequestDto;
import com.josh.emailFunctionality.entity.EmailEntity;
import com.josh.emailFunctionality.entity.EmailStatus;
import com.josh.emailFunctionality.helper.EmailServiceHelper;
import com.josh.emailFunctionality.repository.EmailSendRespository;

@Service
@Transactional
public class EmailSendServiceImpl implements IEmailSendService {


	 @Autowired
	 private EmailSendRespository emailRepository;
	 
	 public static int counter = 0;
	 
	 public static int sendCheckCounter = 0;
	 
	 public static boolean dailyLimitExceeded = false;

		public static int limitCounter = 0;
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());

		@Autowired
		ScheduledThreadPoolExecutor scheduledThreadPoolExecutor;
		
		@Autowired
		EmailServiceHelper emailServiceHelper;
		
	
	 @Override
	 @Async("CustomThreadConfig")
	public void sendEmail(EmailRequestDto emailRequestDto) throws Exception {
		 System.out.println("Time :" + timestamp);
			System.out.println("Pool Size :" + scheduledThreadPoolExecutor.getCorePoolSize());

			EmailStatus stat;
			EmailEntity email = saveEmail(emailRequestDto);
			limitCounter++;
			try {
				emailServiceHelper.sendEmailHelper(emailRequestDto.getEmail(), "Final New Delay Subject", "Final Test text");
				stat = EmailStatus.COMPLETED;
				updateEmail(email.getToken(),stat);
			}
			catch(Exception e)
			{
				if(e.getCause() instanceof javax.mail.AuthenticationFailedException)
				{
					System.out.println("To many login attempts please wait for 15 min from now " + LocalDateTime.now());
					try {
						scheduledThreadPoolExecutor.awaitTermination(15, TimeUnit.MINUTES);
						emailServiceHelper.sendEmailHelper(emailRequestDto.getEmail(), "Blocked message", "Test text");
						System.out.println("Email resending done at " + LocalDateTime.now());
						updateEmail(email.getToken(),EmailStatus.COMPLETED);
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}
				else if(e.getMessage().contains("com.sun.mail.smtp.SMTPSendFailedException"))
				{
					try {
						e.printStackTrace();
						System.out.println("*************DailyLimitReached***********");
						//if counter less than size of list
						//set fom username and password to 1st the 2nd and so on
						//increment counter
						//call the schedule method
						//In second incremetal development keep a flag in database to know given email is available or not
						dailyLimitExceeded = true;
						updateEmail(email.getToken(),EmailStatus.FAILED);
						//scheduledThreadPoolExecutor.getQueue().put(this);
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}
				else if(e.getMessage().contains("com.sun.mail.util.MailConnectException"))
				{
					try {
						System.out.println("$$$$$$$$$$$ MailConnectException $$$$$$$$$$$$$$");
						//emailService.updateEmail(email.getToken(),EmailStatus.PENDING);
						//start();
						scheduledThreadPoolExecutor.awaitTermination(10, TimeUnit.MINUTES);
						emailServiceHelper.sendEmailHelper(emailRequestDto.getEmail(), "Subject", "Test text");
						System.out.println("Email resending of commom port error done at " + LocalDateTime.now());
						updateEmail(email.getToken(),EmailStatus.COMPLETED);
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}
				else if(e.getMessage().contains("com.sun.mail.smtp.SMTPAddressFailedException"))
				{
					updateEmail(email.getToken(),EmailStatus.FAILED);
				}
				else {
					System.out.println(e.getMessage());
					System.out.println("$$$$$$$$$$$ Other $$$$$$$$$$$$$$");
					//emailService.updateEmail(email.getToken(),EmailStatus.PENDING);
					//start();
					try {
						scheduledThreadPoolExecutor.awaitTermination(10, TimeUnit.MINUTES);
						emailServiceHelper.sendEmailHelper(emailRequestDto.getEmail(), "Final New Delay Subject", "Final Test text");
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					
				}
			}	
	}

	
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
	
	public Map<String,EmailStatus> getAllStatusByToken(String[] tkns)
	{
		Map<String,EmailStatus> emailEntities = new HashMap<>();
		for(String token : tkns)
		{
			emailEntities.put(token,emailRepository.findByToken(token).getStatus());
		}
		return emailEntities;
	}

	@Override
	public List<EmailEntity> getbyStatus(EmailStatus status) {
		return emailRepository.findByStatus(status);
	}


	
	


	

}
