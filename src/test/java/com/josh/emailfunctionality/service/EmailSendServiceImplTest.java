package com.josh.emailfunctionality.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import javax.mail.AuthenticationFailedException;
import javax.mail.internet.MimeMessage;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.GreenMailUtil;
import com.icegreen.greenmail.util.ServerSetupTest;
import com.josh.emailFunctionality.dto.EmailRequestDto;
import com.josh.emailFunctionality.dto.EmailStatusResponseDto;
import com.josh.emailFunctionality.entity.EmailEntity;
import com.josh.emailFunctionality.entity.EmailStatus;
import com.josh.emailFunctionality.helper.EmailServiceHelper;
import com.josh.emailFunctionality.repository.EmailSendRespository;
import com.josh.emailFunctionality.repository.RegisterEmailRepository;
import com.josh.emailFunctionality.service.EmailSendServiceImpl;
import com.josh.emailfunctionality.commons.CommonResourse;
import com.sun.mail.smtp.SMTPAddressFailedException;
import com.sun.mail.smtp.SMTPSendFailedException;
import com.sun.mail.util.MailConnectException;
import com.sun.mail.util.SocketConnectException;

@SpringBootTest(classes = EmailSendServiceImpl.class)
@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
public class EmailSendServiceImplTest {

	public GreenMail server;

	public EmailSendServiceImplTest() {
		ServerSetupTest.setPortOffset(2524);
		server = new GreenMail(ServerSetupTest.SMTP);
	}

	@Autowired
	EmailServiceHelper emailHelper;

	@Autowired
	EmailSendServiceImpl emailService;

	@MockBean
	EmailSendRespository emailRepoMockBean;
	@MockBean
	EmailServiceHelper emailHelperMock;
	@MockBean
	private ScheduledThreadPoolExecutor scheduledThreadPoolExecutor;
	@MockBean
	private RegisterEmailRepository emailRegRepo;
	@Autowired
	private EmailSendRespository emailSendRepo;

	//@Test
	public void sendEmailTest() throws Exception {
		EmailRequestDto emailReqDto = CommonResourse.getEmailRequestDto();
		EmailEntity emailCustom = CommonResourse.getEmailEntity();
		when(emailHelper.sendEmailHelper(emailReqDto.getEmail(), emailReqDto.getToken()))
				.thenAnswer(new Answer<String>() {

					@Override
					public String answer(InvocationOnMock invocation) throws Throwable {
						GreenMailUtil.sendTextEmailTest(invocation.getArgument(0), "from@gmail.com", "sub", "body");
						return "Mail sending successfull";
					}
				});
		try {
			when(emailRegRepo.findAllIsAvailable()).thenReturn(CommonResourse.getAllEmails());
			when(emailRepoMockBean.findByToken("123")).thenReturn(emailCustom);
			server.start();
			emailService.sendEmail(emailCustom);
			MimeMessage[] receivedMessages = server.getReceivedMessages();
			assertEquals(receivedMessages[0].getAllRecipients()[0].toString(), emailReqDto.getEmail());
			assertEquals(receivedMessages[0].getSubject(), "sub");

			doThrow(new Exception("auth failed", new AuthenticationFailedException())).when(emailHelper)
					.sendEmailHelper(emailReqDto.getEmail(), emailReqDto.getToken());
			emailService.sendEmail(emailCustom);

			EmailServiceHelper.failedEmailSender = "test@gmail.com";
			doThrow(new Exception("Daily limit exution",
					new SMTPSendFailedException(null, 0, null, null, null, null, null))).when(emailHelper)
							.sendEmailHelper(emailReqDto.getEmail(), emailReqDto.getToken());
			when(emailRegRepo.findByEmail("test@gmail.com")).thenReturn(CommonResourse.getEmailRegistration());
			emailService.sendEmail(emailCustom);

			doThrow(new Exception("Mail connection failed",
					new MailConnectException(new SocketConnectException("test", null, "localhost", 0, 0))))
							.when(emailHelper).sendEmailHelper(emailReqDto.getEmail(), emailReqDto.getToken());
			emailService.sendEmail(emailCustom);

			doThrow(new Exception(new SMTPAddressFailedException(null, null, 0, null))).when(emailHelper)
					.sendEmailHelper(emailReqDto.getEmail(), emailReqDto.getToken());
			emailService.sendEmail(emailCustom);

			doThrow(new Exception("For final common exception", new IllegalStateException())).when(emailHelper)
					.sendEmailHelper(emailReqDto.getEmail(), emailReqDto.getToken());
			emailService.sendEmail(emailCustom);
			when(emailRegRepo.findAllIsAvailable()).thenReturn(new ArrayList<>());
			emailService.sendEmail(emailCustom);

			server.stop();
		} catch (Exception ex) {
			System.out.println("Exception " + ex.getMessage());
		}

	}

	//@Test
	public void saveEmailTest() {
		EmailRequestDto emailReqDto = CommonResourse.getEmailRequestDto();
		EmailEntity emailEntity = CommonResourse.getEmailEntity();
		when(emailSendRepo.findByToken("123")).thenReturn(emailEntity);
		when(emailSendRepo.save(emailEntity)).thenReturn(emailEntity);
		EmailEntity em = emailService.saveEmail(emailReqDto);
		assertEquals(em.getEmail(), emailReqDto.getEmail());

		when(emailSendRepo.findByToken("123")).thenReturn(null);
		when(emailSendRepo.save(emailEntity)).thenReturn(emailEntity);
		em = emailService.saveEmail(emailReqDto);

		assertEquals(em, null);
	}

	@Test
	public void getAllStatusByTokenTest() {
		EmailEntity emailEntity = CommonResourse.getEmailEntity();
		emailEntity.setStatus(EmailStatus.COMPLETED);

		when(emailRepoMockBean.findByToken("123")).thenReturn(emailEntity);
		String[] arr = { "123" };
		Map<String, EmailStatusResponseDto> emailEntities = emailService.getAllStatusByToken(arr);
		System.out.println(emailEntities);
		//assertEquals(emailEntities.get("123"), EmailStatus.COMPLETED);

		emailEntities = emailService.getAllStatusByToken(arr);
		
		//assertEquals(emailEntities.get("123"), EmailStatus.NOTFOUND);
	}

}
