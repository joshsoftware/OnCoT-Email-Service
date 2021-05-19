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
import com.josh.emailFunctionality.entity.EmailRegistration;
import com.josh.emailFunctionality.entity.EmailStatus;
import com.josh.emailFunctionality.helper.EmailServiceHelper;
import com.josh.emailFunctionality.repository.EmailSendRespository;
import com.josh.emailFunctionality.repository.RegisterEmailRepository;


@Service
@Transactional
public class EmailSendServiceImpl implements IEmailSendService {


	 @Autowired
	 private EmailSendRespository emailRepository;
	 
	 @Autowired
	 private RegisterEmailRepository emailRegRepo;
	 public static int sendCheckCounter = 0;
	 public static int dailyLimitExceptionCounter = 0;
	 public static int emailsSize = 0; 
	 
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
		
		 if(emailRegRepo.findAllIsAvailable().size()==0)
			 updateEmail(emailRequestDto.getToken(),EmailStatus.FAILED,"");
		 else
		 {
			 EmailStatus stat;
				String senderEmail="";
				EmailEntity email = new EmailEntity(emailRequestDto);
				try {
					senderEmail =
							  emailServiceHelper.sendEmailHelper(emailRequestDto.getEmail(),emailRequestDto
							  .getToken()); 
					stat = EmailStatus.COMPLETED;
					email.setSender(senderEmail); 
					updateEmail(email.getToken(),stat,senderEmail);
				}
				catch(Exception e)
				{
					if(e.getCause() instanceof javax.mail.AuthenticationFailedException)
					{
						try {
							scheduledThreadPoolExecutor.awaitTermination(15, TimeUnit.MINUTES);
							String sender = emailServiceHelper.sendEmailHelper(emailRequestDto.getEmail(), emailRequestDto.getToken());
							email.setSender(sender);
							updateEmail(email.getToken(),EmailStatus.COMPLETED,sender);
						} catch (Exception e1) {
							e1.printStackTrace();
						}
					}
					else if(e.getMessage().contains("com.sun.mail.smtp.SMTPSendFailedException"))
					{
						try {
							e.printStackTrace();
							dailyLimitExceptionCounter++;
							resetDaiyLimit(senderEmail);
							changeAvailableStatus(senderEmail, false);
							if(dailyLimitExceptionCounter < emailsSize)
							{
								senderEmail = emailServiceHelper.sendEmailHelper(emailRequestDto.getEmail(),emailRequestDto
										  .getToken()); 
								stat = EmailStatus.COMPLETED;
								updateEmail(email.getToken(),stat,senderEmail);
							} 
							else {
								updateEmail(email.getToken(),EmailStatus.FAILED,"");							
							}
							dailyLimitTimestamp = System.currentTimeMillis();
						} catch (Exception e1) {
							e1.printStackTrace();
						}
					}
					else if(e.getMessage().contains("com.sun.mail.util.MailConnectException"))
					{
						try {
							scheduledThreadPoolExecutor.awaitTermination(10, TimeUnit.MINUTES);
							senderEmail = emailServiceHelper.sendEmailHelper(emailRequestDto.getEmail(), emailRequestDto.getToken());
							email.setSender(senderEmail);
							updateEmail(email.getToken(),EmailStatus.COMPLETED,senderEmail);
						} catch (Exception e1) {
							e1.printStackTrace();
						}
					}
					else if(e.getMessage().contains("com.sun.mail.smtp.SMTPAddressFailedException"))
					{
						updateEmail(email.getToken(),EmailStatus.FAILED,"");
					}
					else {
						e.printStackTrace();
						try {
							updateEmail(email.getToken(),EmailStatus.FAILED,"");
						} catch (Exception e1) {
							e1.printStackTrace();
						}
						
					}
				}
		 }	
	}

	
	public EmailEntity saveEmail( EmailRequestDto emailRequestDto) {
		EmailEntity alreadyExist = emailRepository.findByToken(emailRequestDto.getToken());
		if(alreadyExist == null) {
			EmailEntity newEmail = new EmailEntity(emailRequestDto);
			newEmail.setStatus(EmailStatus.INPROGRESS);
			EmailEntity savedEmail = emailRepository.save(newEmail);  
			return savedEmail;
		}
		else {
			return alreadyExist;
		}
	
	}
	
	public void changeAvailableStatus(String email,boolean status)
	{
		EmailRegistration emailReg = emailRegRepo.findByEmail(email);
		emailReg.setAvailable(status);
		emailRegRepo.save(emailReg);
	}
	
	public EmailEntity updateEmail(String token,EmailStatus status,String sender) {
		EmailEntity updateEmail = emailRepository.findByToken(token);
		updateEmail.setStatus(status);
		updateEmail.setSender(sender);
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

	public void resetDaiyLimit(String sender) {
		
		Thread th = new Thread(new Runnable() {
			
			@Override
			public void run() {
				timer.schedule(new TimerTask() {
					@Override
					public void run() {
						dailyLimitExceptionCounter--;
						dailyLimitTimestamp = 0;
						if(emailRegRepo.findByEmail(sender)!= null)
							changeAvailableStatus(sender, true);
					}
				}, 60000*4);
			}
		});
		th.start();
	}
}
