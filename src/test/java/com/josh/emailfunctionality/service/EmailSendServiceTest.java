package com.josh.emailfunctionality.service;
import org.junit.jupiter.api.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;

import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.GreenMailUtil;
import com.icegreen.greenmail.util.ServerSetupTest;
import com.josh.emailFunctionality.dto.EmailRequestDto;
import com.josh.emailFunctionality.entity.EmailEntity;
import com.josh.emailFunctionality.helper.EmailServiceHelper;
import com.josh.emailFunctionality.repository.EmailSendRespository;
import com.josh.emailFunctionality.service.IEmailRegisterService;
import com.josh.emailFunctionality.service.IEmailSendService;
import freemarker.template.Configuration;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import javax.mail.internet.MimeMessage;

@WebMvcTest(EmailSendServiceTest.class)
@ContextConfiguration(classes={SpringBootApplication.class})
public class EmailSendServiceTest {
	
	
	public GreenMail server;	
	public EmailSendServiceTest() {
		ServerSetupTest.setPortOffset(2524);
		server = new GreenMail(ServerSetupTest.SMTP);
		server.start();

	}
	
	@Autowired
	EmailServiceHelper emailHelper;
	@Autowired
	IEmailSendService emailService;
	
	@MockBean
	EmailSendRespository emailRepoMockBean;
	@MockBean
	EmailServiceHelper emailHelperMock;
	@MockBean
	IEmailSendService emailServMock;
	@MockBean
	IEmailRegisterService emailRegiserService;
	@MockBean
	Configuration configuation;
	@Autowired
	private EmailSendRespository emailSendRepo;
	
	
	@Test
	public void sendEmailTest() throws Exception
	{
		EmailRequestDto emailReqDto  = new EmailRequestDto("test17061997@gmail.com","123");
		when(emailHelper.sendEmailHelper(emailReqDto.getEmail(), emailReqDto.getToken())).thenAnswer(new Answer<String>() {

			@Override
			public String answer(InvocationOnMock invocation) throws Throwable {
				GreenMailUtil.sendTextEmailTest(invocation.getArgument(0), "from@gmail.com", "sub", "body");
				return "Mail sending successfull";
			}
		});
		try {
			server.start();
			emailHelper.sendEmailHelper(emailReqDto.getEmail(), emailReqDto.getToken());
		}catch(Exception ex)
		{
			System.out.println("Exception " + ex.getMessage());
		}
		MimeMessage[] receivedMessages = server.getReceivedMessages();
		assertEquals(receivedMessages[0].getAllRecipients()[0].toString(),emailReqDto.getEmail());
		assertEquals(receivedMessages[0].getSubject(), "sub");
	}
		
	@Test
	public void saveEmail()
	{
		EmailRequestDto emailReqDto  = new EmailRequestDto("test17061997@gmail.com","123");
		EmailEntity emailEntity = new EmailEntity(new EmailRequestDto("test17061997@gmail.com","123"));
		when(emailSendRepo.findByToken("123")).thenReturn(emailEntity);
		when(emailSendRepo.save(emailEntity)).thenReturn(emailEntity);
		when(emailService.saveEmail(emailReqDto)).thenReturn(emailEntity);
		EmailEntity em = emailService.saveEmail(emailReqDto);
		assertEquals(em.getEmail(), emailReqDto.getEmail());
	}
	
}
