package com.josh.emailFunctionality.service;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
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
	 
	 
	 public static int sendCheckCounter = 0;
	 public static int dailyLimitExceptionCounter = 0;
	 public static int emailsSize = 0; 
	 public static boolean dailyLimitExceeded = false;

		public static int limitCounter = 0;
		public static long dailyLimitTimestamp;
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());

		@Autowired
		ScheduledThreadPoolExecutor scheduledThreadPoolExecutor;
		
		@Autowired
		EmailServiceHelper emailServiceHelper;
		
	
	 @Override
	 @Async("CustomThreadConfig")
	public void sendEmail(EmailRequestDto emailRequestDto) throws Exception {
			EmailStatus stat;
			EmailEntity email = new EmailEntity(emailRequestDto);
			try {
				String sender = emailServiceHelper.sendEmailHelper(emailRequestDto.getEmail(),emailRequestDto.getToken());
				email.setSender(sender);
				stat = EmailStatus.COMPLETED;
				updateEmail(email.getToken(),stat);
			}
			catch(Exception e)
			{
				if(e.getCause() instanceof javax.mail.AuthenticationFailedException)
				{
					try {
						scheduledThreadPoolExecutor.awaitTermination(15, TimeUnit.MINUTES);
						String sender = emailServiceHelper.sendEmailHelper(emailRequestDto.getEmail(), emailRequestDto.getToken());
						email.setSender(sender);
						updateEmail(email.getToken(),EmailStatus.COMPLETED);
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}
				else if(e.getMessage().contains("com.sun.mail.smtp.SMTPSendFailedException"))
				{
					try {
						e.printStackTrace();
						dailyLimitExceeded = true;
						resetDaiyLimit();
						updateEmail(email.getToken(),EmailStatus.FAILED);
						dailyLimitTimestamp = System.currentTimeMillis();
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}
				else if(e.getMessage().contains("com.sun.mail.util.MailConnectException"))
				{
					try {
						scheduledThreadPoolExecutor.awaitTermination(10, TimeUnit.MINUTES);
						String sender = emailServiceHelper.sendEmailHelper(emailRequestDto.getEmail(), emailRequestDto.getToken());
						email.setSender(sender);
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
					e.printStackTrace();
					try {
						updateEmail(email.getToken(),EmailStatus.FAILED);
					} catch (Exception e1) {
						e1.printStackTrace();
					}
					
				}
			}	
	}

	
	public EmailEntity saveEmail( EmailRequestDto emailRequestDto) {
		EmailEntity sample = emailRepository.findByToken(emailRequestDto.getToken());
		if(sample == null) {
			EmailEntity email = new EmailEntity(emailRequestDto);
			email.setStatus(EmailStatus.INPROGRESS);
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
	
	private Timer timer = new Timer();

	public void resetDaiyLimit() {
		System.out.println("dailyLimitExceeded flag : " + dailyLimitExceeded);
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				System.out.println("Logic successfull");
				dailyLimitExceeded = false;
				dailyLimitTimestamp = 0;
			}
		}, 24*60*60*1000);
	}
}
