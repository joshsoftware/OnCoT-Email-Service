package com.josh.emailFunctionality.service;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.josh.emailFunctionality.dto.EmailRequestDto;
import com.josh.emailFunctionality.dto.EmailStatusResponseDto;
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

	
	//new method to renew the availability at application startup time
	@PostConstruct
	public void makeSenderEmailAvailable()
	{
		TimeZone.setDefault(TimeZone.getTimeZone("IST"));
		for(EmailRegistration emailRegistration:emailRegRepo.findAll())
		{
		  if(!emailRegistration.isAvailable())
		  {
			  emailRegistration.setAvailable(true);
			  emailRegRepo.save(emailRegistration);
		  }
		}	
	}
	
	//This method is used to send emails to the registered candidates
	@Override
	@Async("CustomThreadConfig")
	public void sendEmail(EmailEntity emailCustom) throws Exception {
		List<EmailRegistration> availableEmails = emailRegRepo.findAllIsAvailable();

		if (availableEmails.size() == 0) {
			updateEmail(emailCustom.getToken(), EmailStatus.FAILED, "", null);
			areSendersAvailable = false;
		} else {
			EmailStatus stat;
			Timestamp time;
			String senderEmail = "";
			try {
				String sender = emailServiceHelper.sendEmailHelper(emailCustom.getEmail(), emailCustom.getToken());
				stat = EmailStatus.COMPLETED;
				emailCustom.setSender(sender);
				emailCustom.setStatus(stat);
				time = new Timestamp(System.currentTimeMillis());
				emailCustom.setTimeStamp(time);
				updateEmail(emailCustom.getToken(), stat, sender, time);
				System.out.println("Email Sent : " + emailCount);
				emailCount++;
			} catch (Exception e) {
				System.out.println(e.getCause());
				if (e.getCause() instanceof javax.mail.AuthenticationFailedException) {
					logger.error(" In exception of AuthenticationFailedException ");
					try {
						scheduledThreadPoolExecutor.awaitTermination(15, TimeUnit.MINUTES);
						String sender = emailServiceHelper.resendEmail(emailCustom);
						time = new Timestamp(System.currentTimeMillis());
						updateEmail(emailCustom.getToken(), EmailStatus.COMPLETED, sender, time);
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				} else if (e.getCause().toString().contains("com.sun.mail.smtp.SMTPSendFailedException")) {
					try {
						logger.error("In exception of SMTPSendFailedException daily limit failed for "
								+ EmailServiceHelper.failedEmailSender);
						e.printStackTrace();
						resetDailyLimit(EmailServiceHelper.failedEmailSender);
						changeAvailableStatus(EmailServiceHelper.failedEmailSender, false);
						if (emailRegRepo.findAllIsAvailable().size() != 0) {
							String sender = emailServiceHelper.resendEmail(emailCustom);
							stat = EmailStatus.COMPLETED;
							time = new Timestamp(System.currentTimeMillis());
							updateEmail(emailCustom.getToken(), stat, sender, time);
						} else {
							updateEmail(emailCustom.getToken(), EmailStatus.FAILED, "", null);
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
						time = new Timestamp(System.currentTimeMillis());
						updateEmail(emailCustom.getToken(), EmailStatus.COMPLETED, senderEmail, time);
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				} else if (e.getCause().toString().contains("com.sun.mail.smtp.SMTPAddressFailedException")) {
					logger.error("In exception of SMTPAddressFailedException");
					updateEmail(emailCustom.getToken(), EmailStatus.FAILED, "", null);
				} else {
					logger.error("Other exception " + e.getMessage());
					e.printStackTrace();
					try {
						updateEmail(emailCustom.getToken(), EmailStatus.FAILED, "", null);
					} catch (Exception e1) {
						e1.printStackTrace();
					}

				}
			}
		}
	}

	// This method is used to save the emails of candidates in the database
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

	// This is used to change the staus of pending emails
	public void changeAvailableStatus(String email, boolean status) {
		EmailRegistration emailReg = emailRegRepo.findByEmail(email);
		System.out.println(emailReg);
		emailReg.setAvailable(status);
		emailRegRepo.save(emailReg);
	}

	// Used to update the emails that are in database
	public EmailEntity updateEmail(String token, EmailStatus status, String sender, Date time) {
		EmailEntity updateEmail = emailRepository.findByToken(token);
		updateEmail.setStatus(status);
		updateEmail.setSender(sender);
		updateEmail.setTimeStamp(time);
		emailRepository.save(updateEmail);
		return updateEmail;
	}

	// To get the status of emails from database
	public Map<String, EmailStatusResponseDto> getAllStatusByToken(String[] tkns) {
		Map<String, EmailStatusResponseDto> emailEntities = new HashMap<String, EmailStatusResponseDto>();
		for (String token : tkns) {
			EmailEntity emailEntity = emailRepository.findByToken(token);
			EmailStatusResponseDto emailStatusEntities = new EmailStatusResponseDto();
			if (emailEntity != null) {
				if(emailEntity.getTimeStamp()!=null)
					emailStatusEntities.setSentAt(emailEntity.getTimeStamp().toString());
				else
					emailStatusEntities.setSentAt(null);
				emailStatusEntities.setStatus(emailEntity.getStatus());
				emailStatusEntities.setSender(emailEntity.getSender());
				emailEntities.put(token, emailStatusEntities);
			} else {
				emailStatusEntities.setStatus(EmailStatus.NOTFOUND);
				emailEntities.put(token, emailStatusEntities);
			}

		}
		return emailEntities;
	}

	// To reset the daily limit
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
