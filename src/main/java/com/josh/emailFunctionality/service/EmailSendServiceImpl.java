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
	public static boolean areSendersAvailable = true;
	public static long dailyLimitTimestamp;
	Timestamp timestamp = new Timestamp(System.currentTimeMillis());

	@Autowired
	private ScheduledThreadPoolExecutor scheduledThreadPoolExecutor;

	@Autowired
	private EmailServiceHelper emailServiceHelper;

	@Override
	@Async("CustomThreadConfig")
	public void sendEmail(EmailEntity emailCustom) throws Exception {
		if (emailRegRepo.findAllIsAvailable().size() == 0) {
			updateEmail(emailCustom.getToken(), EmailStatus.FAILED, "");
			areSendersAvailable = false;
		} else {
			EmailStatus stat;
			String senderEmail = "";
			try {
				String sender = emailServiceHelper.sendEmailHelper(emailCustom.getEmail(), emailCustom.getToken());
				stat = EmailStatus.COMPLETED;
				emailCustom.setSender(sender);
				updateEmail(emailCustom.getToken(), stat, sender);
			} catch (Exception e) {
				if (e.getCause() instanceof javax.mail.AuthenticationFailedException) {
					try {
						scheduledThreadPoolExecutor.awaitTermination(15, TimeUnit.MINUTES);
						String sender = emailServiceHelper.sendEmailHelper(emailCustom.getEmail(),
								emailCustom.getToken());
						emailCustom.setSender(sender);
						updateEmail(emailCustom.getToken(), EmailStatus.COMPLETED, sender);
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				} else if (e.getCause().toString().contains("com.sun.mail.smtp.SMTPSendFailedException")) {
					try {
						System.out.println("Failed email :" + EmailServiceHelper.failedEmailSender);
						e.printStackTrace();
						resetDailyLimit(EmailServiceHelper.failedEmailSender);
						changeAvailableStatus(EmailServiceHelper.failedEmailSender, false);
						if (emailRegRepo.findAllIsAvailable().size() != 0) {
							String sender = emailServiceHelper.sendEmailHelper(emailCustom.getEmail(),
									emailCustom.getToken());
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
						scheduledThreadPoolExecutor.awaitTermination(10, TimeUnit.MINUTES);
						senderEmail = emailServiceHelper.sendEmailHelper(emailCustom.getEmail(),
								emailCustom.getToken());
						emailCustom.setSender(senderEmail);
						updateEmail(emailCustom.getToken(), EmailStatus.COMPLETED, senderEmail);
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				} else if (e.getCause().toString().contains("com.sun.mail.smtp.SMTPAddressFailedException")) {
					updateEmail(emailCustom.getToken(), EmailStatus.FAILED, "");
				} else {
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

	public void changeAvailableStatus(String email, boolean status) {
		EmailRegistration emailReg = emailRegRepo.findByEmail(email);
		emailReg.setAvailable(status);
		emailRegRepo.save(emailReg);
	}

	public EmailEntity updateEmail(String token, EmailStatus status, String sender) {
		EmailEntity updateEmail = emailRepository.findByToken(token);
		updateEmail.setStatus(status);
		updateEmail.setSender(sender);
		emailRepository.save(updateEmail);
		return updateEmail;
	}

	public Map<String, EmailStatus> getAllStatusByToken(String[] tkns) {
		Map<String, EmailStatus> emailEntities = new HashMap<>();
		for (String token : tkns) {
			emailEntities.put(token, emailRepository.findByToken(token).getStatus());
		}
		return emailEntities;
	}

	@Override
	public List<EmailEntity> getbyStatus(EmailStatus status) {
		return emailRepository.findByStatus(status);
	}

	private Timer timer = new Timer();

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
