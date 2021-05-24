package com.josh.emailFunctionality.service;

import java.util.List;
import java.util.Properties;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.josh.emailFunctionality.configuration.ThreadPoolTaskSchedulerConfig;
import com.josh.emailFunctionality.dto.EmailRegisterRequestDto;
import com.josh.emailFunctionality.entity.EmailRegistration;
import com.josh.emailFunctionality.helper.EmailRegisterHelper;
import com.josh.emailFunctionality.helper.EncryptionDecryptionHelper;
import com.josh.emailFunctionality.repository.RegisterEmailRepository;

//This service deals with sender emails and EmailRegistration.java
@Service
@Transactional
public class EmailRegisterServiceImpl implements IEmailRegisterService {

	@Autowired
	private RegisterEmailRepository registerEmailRepository;

	@Autowired
	private EmailRegisterHelper emailRegHelper;

	@Autowired
	private EncryptionDecryptionHelper helper;

	//This method is used to get all sender emails from the database
	@Override
	public List<EmailRegistration> getAllEmails() {
		List<EmailRegistration> availableEmails = registerEmailRepository.findAll();
		return availableEmails;
	}

	//This method is used to add new sender emails in the database
	@Override
	public EmailRegistration addEmail(EmailRegisterRequestDto regEmailReqDto) {
		regEmailReqDto.setPassword(helper.encrypt(regEmailReqDto.getPassword()));
		EmailRegistration emailReg = registerEmailRepository.save(new EmailRegistration(regEmailReqDto));
		List<EmailRegistration> emails = getAllEmails();
		ScheduledThreadPoolExecutor newScheduledThreadPoolExec = emailRegHelper.reinitiateThreadPool(emails.size());
		new ThreadPoolTaskSchedulerConfig().reintialiseBean(newScheduledThreadPoolExec, emails.size());
		EmailSendServiceImpl.areSendersAvailable = true;
		return emailReg;
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

	//This method is used to delete the sender emails from the database
	@Override
	public EmailRegistration deleteEmail(long id) {
		EmailRegistration emReg = registerEmailRepository.findById(id).get();
		if (emReg != null) {
			registerEmailRepository.deleteById(id);
			List<EmailRegistration> emails = getAllEmails();
			int size;
			if (emails.size() == 0) {
				size = 1;
			} else {
				size = emails.size();
			}
			ScheduledThreadPoolExecutor newScheduledThreadPoolExec = emailRegHelper.reinitiateThreadPool(size);
			new ThreadPoolTaskSchedulerConfig().reintialiseBean(newScheduledThreadPoolExec, emails.size());
			return emReg;
		} else {
			return null;
		}
	}
}
