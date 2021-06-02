package com.josh.emailfunctionality.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.context.WebApplicationContext;

import com.josh.emailFunctionality.EmailFunctionalityApplication;
import com.josh.emailFunctionality.dto.EmailRegisterRequestDto;
import com.josh.emailFunctionality.entity.EmailRegistration;
import com.josh.emailFunctionality.helper.EmailRegisterHelper;
import com.josh.emailFunctionality.helper.EncryptionDecryptionHelper;
import com.josh.emailFunctionality.repository.RegisterEmailRepository;
import com.josh.emailFunctionality.service.EmailRegisterServiceImpl;
import com.josh.emailfunctionality.commons.CommonResourse;

@SpringBootTest(classes = EmailRegisterServiceImpl.class)
@RunWith(SpringJUnit4ClassRunner.class)
public class EmailRegisterServiceImplTest {

	@Autowired
	WebApplicationContext webApplicationContext;

	@Autowired
	EmailRegisterServiceImpl emailRegisterServiceImpl;

	@MockBean
	EmailRegisterHelper emailRegHelper;

	@MockBean
	EncryptionDecryptionHelper encrypHelperMock;

	@MockBean
	RegisterEmailRepository registerEmailRepository;

	@BeforeEach
	public void initiateContext() {
		ConfigurableApplicationContext context = SpringApplication.run(EmailFunctionalityApplication.class,
				"--server.port=0", "--spring.jmx.enabled=false");
		EmailFunctionalityApplication.context = context;
	}

	@Test
	public void addEmailTest() {
		EmailRegisterRequestDto regEmailReqDto = CommonResourse.getEmailRegistrationRequestDto();
		when(registerEmailRepository.save(CommonResourse.getNewEmailRegistration()))
				.thenReturn(CommonResourse.getEmailRegistration());
		when(registerEmailRepository.findAll()).thenReturn(CommonResourse.getAllEmails());
		when(emailRegHelper.reinitiateThreadPool(1)).thenReturn(CommonResourse.getThreadPoolExecutor());
		when(encrypHelperMock.encrypt(regEmailReqDto.getPassword())).thenReturn("test");
		EmailRegistration emailRegistration = emailRegisterServiceImpl.addEmail(regEmailReqDto);
		assertEquals(emailRegistration.getEmail(), regEmailReqDto.getEmail());
	}

	@Test
	public void deleteEmailTest() {
		EmailRegistration emReg = new EmailRegistration(1L, "test@gmail.com", "test", true);
		when(registerEmailRepository.findById(1L)).thenReturn(Optional.of(emReg));
		when(emailRegHelper.reinitiateThreadPool(1)).thenReturn(CommonResourse.getThreadPoolExecutor());
		EmailRegistration emailRegistration = emailRegisterServiceImpl.deleteEmail(1L);
		assertEquals(emailRegistration.getEmail(), emReg.getEmail());
	}

}
