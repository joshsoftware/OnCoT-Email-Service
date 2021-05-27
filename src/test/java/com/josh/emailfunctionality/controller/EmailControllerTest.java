package com.josh.emailfunctionality.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.josh.emailFunctionality.controller.EmailController;
import com.josh.emailFunctionality.entity.EmailRegistration;
import com.josh.emailFunctionality.entity.EmailStatus;
import com.josh.emailFunctionality.helper.EmailTemplateHelper;
import com.josh.emailFunctionality.service.IEmailRegisterService;
import com.josh.emailFunctionality.service.IEmailSendService;
import com.josh.emailfunctionality.commons.CommonResourse;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = EmailController.class)
@ContextConfiguration
@WebAppConfiguration
@EnableWebMvc
public class EmailControllerTest {

	protected String mapToJson(Object reqDto) throws JsonProcessingException {
		ObjectMapper objectMapper = new ObjectMapper();
		return objectMapper.writeValueAsString(reqDto);
	}

	protected <T> T mapFromJson(String json, Class<T> clazz)
			throws JsonParseException, JsonMappingException, IOException {
		ObjectMapper objectMapper = new ObjectMapper();
		return objectMapper.readValue(json, clazz);
	}

	@MockBean
	IEmailSendService emailService;
	@MockBean
	IEmailRegisterService emailRegService;

	@MockBean
	EmailTemplateHelper emailTemplateHelper;
	protected MockMvc mvc;
	@Autowired
	WebApplicationContext webApplicationContext;

	@BeforeEach
	public void buildMvcEnviorment() {
		mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
	}

	@Test
	void testEmailSendFunctionality() throws Exception {
		String input = mapToJson(CommonResourse.getEmailRequestDto());
		when(emailService.saveEmail(CommonResourse.getEmailRequestDto())).thenReturn(CommonResourse.getEmailEntity());
		when(emailRegService.getAllEmails()).thenReturn(CommonResourse.getAllEmails());

		RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/api/v1/email")
				.contentType(MediaType.APPLICATION_JSON_VALUE).content(input);
		MvcResult mvcRes = mvc.perform(requestBuilder).andReturn();
		assertEquals(mvcRes.getResponse().getStatus(), 201);
		assertEquals(mvcRes.getResponse().getContentAsString().contains("Success"), true);
	}

	@Test
	public void testMultipleEmailSendFunctionality() throws Exception {
		String input = mapToJson(CommonResourse.getEmailArrayRequestDto());
		when(emailService.saveEmail(CommonResourse.getEmailRequestDto())).thenReturn(CommonResourse.getEmailEntity());
		when(emailRegService.getAllEmails()).thenReturn(CommonResourse.getAllEmails());
		RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/api/v1/secondary")
				.contentType(MediaType.APPLICATION_JSON_VALUE).content(input);
		MvcResult mvcRes = mvc.perform(requestBuilder).andReturn();
		System.out.println(mvcRes.getResponse().getContentAsString());
	}

	@Test
	void testGetRegisteredEmails() throws Exception {
		when(emailRegService.getAllEmails()).thenReturn(CommonResourse.getAllEmails());
		RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/api/v1/emails");
		MvcResult mvcRes = mvc.perform(requestBuilder).andReturn();

		assertEquals(mvcRes.getResponse().getStatus(), 200);
		try {
			EmailRegistration[] emails = mapFromJson(mvcRes.getResponse().getContentAsString(),
					EmailRegistration[].class);
			assertEquals(emails.length, 1);
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
		}
	}

	@Test
	public void getStatusOfEmailsTest() throws Exception {
		Map<String, EmailStatus> emailEntities = new HashMap<>();
		emailEntities.put("123", EmailStatus.COMPLETED);
		String[] tkns = { "123" };
		when(emailService.getAllStatusByToken(tkns)).thenReturn(emailEntities);
		RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/api/v1/status")
				.contentType(MediaType.APPLICATION_JSON_VALUE).content("[123]");
		MvcResult mvcRes = mvc.perform(requestBuilder).andReturn();
		@SuppressWarnings("unchecked")
		Map<String, Object> responseEmailEntities = mapFromJson(mvcRes.getResponse().getContentAsString(),
				HashMap.class);
		assertEquals(mvcRes.getResponse().getStatus(), 200);
		assertEquals(responseEmailEntities.get("data").toString().contains("COMPLETED"), true);

	}

	@Test
	public void deleteEmailAccountTest() throws Exception {

		when(emailRegService.deleteEmail(1)).thenReturn(CommonResourse.getEmailRegistration());
		
		RequestBuilder requestBuilder = MockMvcRequestBuilders.delete("/api/v1/email/1");
		MvcResult mvcRes = mvc.perform(requestBuilder).andReturn();
		assertEquals(mvcRes.getResponse().getStatus(), 200);

	}

	@Test
	public void registerEmailAccountTest() throws Exception {
		when(emailRegService.addEmail(CommonResourse.getEmailRegistrationRequestDto()))
				.thenReturn(CommonResourse.getEmailRegistration());

		String input = mapToJson(CommonResourse.getEmailRegistrationRequestDto());

		RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/api/v1/register")
				.contentType(MediaType.APPLICATION_JSON_VALUE).content(input);
		MvcResult mvcRes = mvc.perform(requestBuilder).andReturn();
		System.out.println(mvcRes.getResponse().getStatus());
		assertEquals(mvcRes.getResponse().getStatus(), 200);
	}
}
