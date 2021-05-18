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
import com.josh.emailFunctionality.dto.EmailRegisterReqeustDto;
import com.josh.emailFunctionality.entity.EmailRegistration;
import com.josh.emailFunctionality.helper.EmailRegisterHelper;
import com.josh.emailFunctionality.repository.RegisterEmailRepository;

@Service
@Transactional
public class EmailRegisterServiceImpl implements IEmailRegisterService {

	@Autowired
	RegisterEmailRepository registerEmailRepository;

	@Autowired
	EmailRegisterHelper emailRegHelper;

	@Override
	public List<EmailRegistration> getAllEmails() {
		List<EmailRegistration> availableEmails = registerEmailRepository.findAll();
		return availableEmails;
	}

	@Override
	public EmailRegistration addEmail(EmailRegisterReqeustDto regEmailReqDto) {
		EmailRegistration emailReg = registerEmailRepository.save(new EmailRegistration(regEmailReqDto));
		List<EmailRegistration> emails = getAllEmails();
		ScheduledThreadPoolExecutor newScheduledThreadPoolExec = emailRegHelper.reinitiateThreadPool(emails.size());
		new ThreadPoolTaskSchedulerConfig().reintialiseBean(newScheduledThreadPoolExec);
		return emailReg;
	}

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

	public Session getSession(EmailRegisterReqeustDto regEmailReqDto) {
		System.out.println(regEmailReqDto.getEmail() + "   " + regEmailReqDto.getPassword());

		Session session = Session.getInstance(getProperties(), new Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(regEmailReqDto.getEmail(), regEmailReqDto.getPassword());
			}
		});
		return session;
	}

	@Override
	public void deleteEmail(long id) {
		registerEmailRepository.deleteById(id);
		List<EmailRegistration> emails = getAllEmails();
		int size;
		if(emails.size()==0)
			size = 1;
		else
			size = emails.size();
		ScheduledThreadPoolExecutor newScheduledThreadPoolExec = emailRegHelper.reinitiateThreadPool(size);
		new ThreadPoolTaskSchedulerConfig().reintialiseBean(newScheduledThreadPoolExec);
	}
}
