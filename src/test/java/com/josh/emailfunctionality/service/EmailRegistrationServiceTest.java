package com.josh.emailfunctionality.service;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;

import com.josh.emailFunctionality.dto.EmailRegisterReqeustDto;
import com.josh.emailFunctionality.entity.EmailRegistration;
import com.josh.emailFunctionality.repository.RegisterEmailRepository;
import com.josh.emailFunctionality.service.IEmailRegisterService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;


@WebMvcTest(EmailSendServiceTest.class)
@ContextConfiguration(classes={SpringBootApplication.class})
public class EmailRegistrationServiceTest {

	@MockBean
	IEmailRegisterService emailRegService;

	@MockBean
	RegisterEmailRepository registerEmailRepositoryBean;

	public List<EmailRegistration> getAllEmails() {
		List<EmailRegistration> availableEmails = new ArrayList<>();
		availableEmails.add(new EmailRegistration(1l, "test@gmail.com", "test",true));
		return availableEmails;
	}
	public EmailRegistration getEmailRegistration(EmailRegisterReqeustDto emRDto)
	{
		return new EmailRegistration(emRDto);
	}
	
	@Test
	public void getAllEmailsTest()
	{
		when(emailRegService.getAllEmails()).thenReturn(getAllEmails());
		List<EmailRegistration> emails =  emailRegService.getAllEmails();
		assertEquals(emails.get(0).getId(), 1);
	}
	
	@Test 
	public void addEmailTest()
	{
		EmailRegisterReqeustDto regEmailReqDto = new EmailRegisterReqeustDto("test@gmail.com", "test");
		when(emailRegService.addEmail(regEmailReqDto)).thenReturn(getEmailRegistration(regEmailReqDto));
		EmailRegistration emailRegistration = emailRegService.addEmail(regEmailReqDto);
		assertEquals(emailRegistration.getEmail(), regEmailReqDto.getEmail());
	}

	

    
}
