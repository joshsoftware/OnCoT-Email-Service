package com.josh.emailFunctionality.service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ScheduledThreadPoolExecutor;

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
	
	private ThreadPoolTaskSchedulerConfig threadPoolTaskSchedulerConfig = new ThreadPoolTaskSchedulerConfig();

	//This method is used to get all sender emails from the database
	@Override
	public List<EmailRegistration> getAllEmails() {
		List<EmailRegistration> availableEmails = registerEmailRepository.findAll();
		return availableEmails;
	}
	
	

	//This method is used to add new sender emails in the database
	@Override
	public EmailRegistration addEmail(EmailRegisterRequestDto regEmailReqDto) {
		emailRegHelper.testEmailConnection(regEmailReqDto);		
		regEmailReqDto.setPassword(helper.encrypt(regEmailReqDto.getPassword()));
		EmailRegistration emailReg = registerEmailRepository.save(new EmailRegistration(regEmailReqDto));
		System.out.println(emailReg);
		List<EmailRegistration> emails = getAllEmails();
		ScheduledThreadPoolExecutor newScheduledThreadPoolExec = emailRegHelper.reinitiateThreadPool(emails.size());
		threadPoolTaskSchedulerConfig.reintialiseBean(newScheduledThreadPoolExec, emails.size());
		EmailSendServiceImpl.areSendersAvailable = true;
		return emailReg;
	}


	//This method is used to delete the sender emails from the database
	@Override
	public EmailRegistration deleteEmail(long id) {
		Optional<EmailRegistration> emReg = registerEmailRepository.findById(id);
		if (emReg.isPresent()) {
			registerEmailRepository.deleteById(id);
			List<EmailRegistration> emails = getAllEmails();
			int size;
			if (emails.size() == 0) {
				size = 1;
			} else {
				size = emails.size();
			}
			ScheduledThreadPoolExecutor newScheduledThreadPoolExec = emailRegHelper.reinitiateThreadPool(size);
			threadPoolTaskSchedulerConfig.reintialiseBean(newScheduledThreadPoolExec, emails.size());
			return emReg.get();
		} else {
			return null;
		}
	}
}
