package com.josh.emailFunctionality.controller;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.josh.emailFunctionality.Exception.NoEmailAccountsRegisteredException;
import com.josh.emailFunctionality.common.Response;
import com.josh.emailFunctionality.dto.EmailArrayRequestDto;
import com.josh.emailFunctionality.dto.EmailRegisterRequestDto;
import com.josh.emailFunctionality.dto.EmailRequestDto;
import com.josh.emailFunctionality.entity.EmailEntity;
import com.josh.emailFunctionality.entity.EmailStatus;
import com.josh.emailFunctionality.service.IEmailRegisterService;
import com.josh.emailFunctionality.service.IEmailSendService;

@RestController
@RequestMapping("/api/v1")
@CrossOrigin
public class EmailController {

	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

	@Autowired
	private IEmailSendService emailService;

	@Autowired
	private IEmailRegisterService emailRegisterService;

	private ObjectMapper objectMapper = new ObjectMapper();

	// This api is used to register sender emails
	@PostMapping("/register")
	public ResponseEntity<Response> registerEmailAccount(@RequestBody EmailRegisterRequestDto regEmailReqDto) {

		emailRegisterService.addEmail(regEmailReqDto);
		Response response = new Response("Success", "Email added in database successfully", "", null,
				LocalDateTime.now().format(formatter));
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	// This api is used to get the list of registered sender emails
	@GetMapping("/emails")
	public ResponseEntity<Response> getRegisteredEmail() {
		Response response = new Response("Success", "Registerd Emails", "", new HashMap<>(),
				LocalDateTime.now().format(formatter));
		response.getData().put("emails", emailRegisterService.getAllEmails());
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	// This api is used to delete the sender emails by id
	@DeleteMapping("/email/{id}")
	public ResponseEntity<Response> deleteEmailAccount(@PathVariable long id) {
		emailRegisterService.deleteEmail(id);
		Response response = new Response("Success", "Email deleted successfully", "", null,
				LocalDateTime.now().format(formatter));
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	// This api is used to send emails to candidate as an array of emails and token
	// object
	@PostMapping("/secondary")
	public ResponseEntity<Response> sendAllEmails(@RequestBody EmailArrayRequestDto emailArrayRequestDto) {
		for (EmailRequestDto emailRequestDto : emailArrayRequestDto.getEmails()) {
			if (emailRegisterService.getAllEmails().size() == 0)
				throw new NoEmailAccountsRegisteredException("Please registered at least 1 email account");
			try {
				EmailEntity currentEmailEntity = emailService.saveEmail(emailRequestDto);
				emailService.sendEmail(currentEmailEntity);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		Response response = new Response("Success", "Email sending is in progress", "", null,
				LocalDateTime.now().format(formatter));
		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

	// This api is used to send emails to candidates per request

	@PostMapping("/email")
	public ResponseEntity<Response> sendEmails(@RequestBody EmailRequestDto emailRequestDto) {
		if (emailRegisterService.getAllEmails().size() == 0)
			throw new NoEmailAccountsRegisteredException("Please registered at least 1 email account");
		try {
			EmailEntity currentEmailEntity = emailService.saveEmail(emailRequestDto);
			emailService.sendEmail(currentEmailEntity);
		} catch (Exception e) {
			e.printStackTrace();
		}
		Response response = new Response("Success", "Email sending is in progress", "", null,
				LocalDateTime.now().format(formatter));
		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

	// This api is used to get the status of emails
	@GetMapping("/status")
	public ResponseEntity<Response> getStatusOfEmails(@RequestBody String tkns) throws IOException {
		String[] tokens = objectMapper.reader().forType(new TypeReference<String[]>() {
		}).readValue(tkns);
		Map<String, EmailStatus> emailStatus = emailService.getAllStatusByToken(tokens);
		Response response = new Response("Success", "Status", "", new HashMap<>(),
				LocalDateTime.now().format(formatter));
		response.getData().put("Status", emailStatus);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
}
