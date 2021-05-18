package com.josh.emailfunctionality.controller;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.josh.emailFunctionality.dto.EmailRequestDto;
import com.josh.emailFunctionality.service.IEmailRegisterService;
import com.josh.emailFunctionality.service.IEmailSendService;
import com.josh.emailfunctionality.service.EmailSendServiceTest;

@WebMvcTest(EmailSendServiceTest.class)
@ContextConfiguration(classes={SpringBootApplication.class})
public class EmailControllerTest {
	
	protected String  mapToJson(EmailRequestDto reqDto) throws JsonProcessingException
	{
		ObjectMapper objectMapper = new ObjectMapper();
	     return objectMapper.writeValueAsString(reqDto);
	}
	
	@MockBean
	IEmailSendService emailService;
	@MockBean
	IEmailRegisterService emailRegService;
	
	protected MockMvc mvc;
	@Autowired
	WebApplicationContext	 webApplicationContext;
	@Test
    void testEmailSendFunctionality() throws Exception {
        mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
		
		com.josh.emailFunctionality.dto.EmailRequestDto testEmailRequestDto = new com.josh.emailFunctionality.dto.EmailRequestDto("a@a.com", "123");
        String input = mapToJson(testEmailRequestDto);
        doNothing().when(emailService).sendEmail(testEmailRequestDto);
        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/api/v1/email").accept(MediaType.APPLICATION_JSON)
                .content(input).contentType(MediaType.APPLICATION_JSON);
        System.out.println(input);
        mvc.perform(requestBuilder);
        
    }
}
