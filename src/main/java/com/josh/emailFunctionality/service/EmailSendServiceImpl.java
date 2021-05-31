package com.josh.emailFunctionality.service;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

//This Service deals with EmailEntity.java
//This handles the functionality of sending emails to registered candidater
@Service
@Transactional
public class EmailSendServiceImpl implements IEmailSendService {

	@Autowired
	private EmailSendRespository emailRepository;

	@Autowired
	private RegisterEmailRepository emailRegRepo;

	public static int sendCheckCounter = 0;
	public static boolean areSendersAvailable = true;
	public static long dailyLimitTimestamp;
	Timestamp timestamp = new Timestamp(System.currentTimeMillis());
	private Timer timer = new Timer();
	public static int emailCount = 0;

	@Autowired
	private ScheduledThreadPoolExecutor scheduledThreadPoolExecutor;

	@Autowired
	private EmailServiceHelper emailServiceHelper;
	
	private Logger logger = LoggerFactory.getLogger(EmailSendServiceImpl.class);

	//This method is used to send emails to the registered candidates
	@Override
	@Async("CustomThreadConfig")
	public void sendEmail(EmailEntity emailCustom) throws Exception {
		List<EmailRegistration> availableEmails  =emailRegRepo.findAllIsAvailable();
		
		if (availableEmails.size()== 0) {
			updateEmail(emailCustom.getToken(), EmailStatus.FAILED, "");
			areSendersAvailable = false;
		} else {
			EmailStatus stat;
			String senderEmail = "";
			try {
				String sender = emailServiceHelper.sendEmailHelper(emailCustom.getEmail(), emailCustom.getToken());
				stat = EmailStatus.COMPLETED;
				emailCustom.setSender(sender);
				emailCustom.setStatus(stat);
				updateEmail(emailCustom.getToken(), stat, sender);
				System.out.println("Email Sent : "+emailCount);
				emailCount++;
			} catch (Exception e) {
				System.out.println(e.getCause());
				if (e.getCause() instanceof javax.mail.AuthenticationFailedException) {
					logger.error(" In exception of AuthenticationFailedException ");
					try {
						scheduledThreadPoolExecutor.awaitTermination(15, TimeUnit.MINUTES);
						String sender = emailServiceHelper.resendEmail(emailCustom);
						updateEmail(emailCustom.getToken(), EmailStatus.COMPLETED, sender);
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				} else if (e.getCause().toString().contains("com.sun.mail.smtp.SMTPSendFailedException")) {
					try {
						logger.error("In exception of SMTPSendFailedException daily limit failed for " + EmailServiceHelper.failedEmailSender);
						e.printStackTrace();
						resetDailyLimit(EmailServiceHelper.failedEmailSender);
						changeAvailableStatus(EmailServiceHelper.failedEmailSender, false);
						if (emailRegRepo.findAllIsAvailable().size() != 0) {
							String sender = emailServiceHelper.resendEmail(emailCustom);
							stat = EmailStatus.COMPLETED;
							updateEmail(emailCustom.getToken(), stat, sender);
						} else {
							updateEmail(emailCustom.getToken(), EmailStatus.FAILED, "");
						}
						dailyLimitTimestamp = System.currentTimeMillis();
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				} else if (e.getCause().toString().contains("com.sun.mail.util.MailConnectException")) {
					try {
						logger.error("In exception of MailConnectException");
						scheduledThreadPoolExecutor.awaitTermination(10, TimeUnit.MINUTES);
						senderEmail = emailServiceHelper.resendEmail(emailCustom);
						updateEmail(emailCustom.getToken(), EmailStatus.COMPLETED, senderEmail);
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				} else if (e.getCause().toString().contains("com.sun.mail.smtp.SMTPAddressFailedException")) {
					logger.error("In exception of SMTPAddressFailedException");
					updateEmail(emailCustom.getToken(), EmailStatus.FAILED, "");
				} else {
					logger.error("Other exception " + e.getMessage());
					e.printStackTrace();
					try {
						updateEmail(emailCustom.getToken(), EmailStatus.FAILED, "");
					} catch (Exception e1) {
						e1.printStackTrace();
					}

				}
			}
		}
	}

	
	//This method is used to save the emails of candidates in the database
	public EmailEntity saveEmail(EmailRequestDto emailRequestDto) {
		EmailEntity alreadyExist = emailRepository.findByToken(emailRequestDto.getToken());
		if (alreadyExist == null) {
			EmailEntity newEmail = new EmailEntity(emailRequestDto);
			newEmail.setStatus(EmailStatus.INPROGRESS);
			EmailEntity savedEmail = emailRepository.save(newEmail);
			return savedEmail;
		} else {
			alreadyExist.setEmail(emailRequestDto.getEmail());
			EmailEntity alreadSavedEmail = emailRepository.save(alreadyExist);
			return alreadSavedEmail;
		}

	}

	//This is used to change the staus of pending emails
	public void changeAvailableStatus(String email, boolean status) {
		EmailRegistration emailReg = emailRegRepo.findByEmail(email);
		System.out.println(emailReg);
		emailReg.setAvailable(status);
		emailRegRepo.save(emailReg);
	}

	//Used to update the emails that are in database
	public EmailEntity updateEmail(String token, EmailStatus status, String sender) {
		EmailEntity updateEmail = emailRepository.findByToken(token);
		updateEmail.setStatus(status);
		updateEmail.setSender(sender);
		emailRepository.save(updateEmail);
		return updateEmail;
	}

	//To get the status of emails from database
	public Map<String, EmailStatus> getAllStatusByToken(String[] tkns) {
		Map<String, EmailStatus> emailEntities = new HashMap<>();
		for (String token : tkns) {
		   EmailEntity emailEntity = emailRepository.findByToken(token);
			if(emailEntity != null)
			{
				emailEntities.put(token,emailEntity.getStatus());
			}
			else
			{
				emailEntities.put(token, EmailStatus.NOTFOUND);
			}
		   
		}
		return emailEntities;
	}

	//To reset the daily limit
	public void resetDailyLimit(String sender) {
		Thread th = new Thread(new Runnable() {
			@Override
			public void run() {
				timer.schedule(new TimerTask() {
					@Override
					public void run() {
						dailyLimitTimestamp = 0;
						if (emailRegRepo.findByEmail(sender) != null)
							areSendersAvailable = true;
						changeAvailableStatus(sender, true);
					}
				}, 60 * 60 * 1000 * 24);
			}
		});
		th.start();
	}
}
