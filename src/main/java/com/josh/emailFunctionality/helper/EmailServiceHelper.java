package com.josh.emailFunctionality.helper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.HtmlEmail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import com.josh.emailFunctionality.Exception.NoEmailAccountsRegisteredException;
import com.josh.emailFunctionality.entity.EmailRegistration;
import com.josh.emailFunctionality.repository.RegisterEmailRepository;
import com.josh.emailFunctionality.service.IEmailRegisterService;

import freemarker.template.Configuration;
import freemarker.template.Template;

@Service
public class EmailServiceHelper {

	@Autowired
	private IEmailRegisterService emailRegisterService;
	
	@Autowired
	private RegisterEmailRepository tempRepo;

	@Autowired
	private Configuration configuation;

	@Autowired
	EncryptionDecryptionHelper helper;

	@Value("${app.email.url}")
	private String testUrl;

	@Value("${app.email.subject}")
	private String subject;
	
	public static int tempCount=0;//Remove this
	
	public static String failedEmailSender="";

	public static int sendCheckCounter = 0;

	public String sendEmailHelper(String to, String token) throws Exception {
//		List<EmailRegistration> sendEm = emailRegisterService.getAllEmails();//Get only those emails that have staus as avilable
		List<EmailRegistration> sendEm = tempRepo.findAllIsAvailable();
//		System.out.println("SendEm size" + sendEm.size());
//		if(sendEm.isEmpty()) {
//			System.out.println("Inside empty logic");
//			throw new NoEmailAccountsRegisteredException("Daily Limit of all emails exceeded");
//		}
//		while (sendEm.get(sendCheckCounter).isAvailable()) {
//
//			sendCheckCounter++;                            //Commented bu Chinmay on Friday
//			if (sendCheckCounter == (sendEm.size())) {
//				sendCheckCounter = 0;
//			System.out.println("sendCheckCounter - " + sendCheckCounter);
//			}
//		}
		HtmlEmail email = new HtmlEmail();
		email.setHostName("smtp.googlemail.com");
		email.setSmtpPort(587);
		email.setAuthenticator(new DefaultAuthenticator(sendEm.get(sendCheckCounter).getEmail(),
				helper.decrypt(sendEm.get(sendCheckCounter).getPassword())));
		email.setSSLOnConnect(true);
		Template template = configuation.getTemplate("emailTemplate.ftl");
		Map<String, Object> model = new HashMap<>();
		model.put("token", token);
		model.put("url", testUrl);
		String templateText = FreeMarkerTemplateUtils.processTemplateIntoString(template, model);
		email.setFrom(sendEm.get(sendCheckCounter).getEmail());
		email.setSubject(subject);
		email.setHtmlMsg(templateText);
		email.addTo(to);
		System.out.println("Sender before : " +email.getFromAddress()+" "+tempCount);
		try {
			email.send();
		}
		catch(Exception e) {
			if(e.getCause().toString().contains("com.sun.mail.smtp.SMTPSendFailedException")){
				failedEmailSender=email.getFromAddress().toString();
			}
			throw e;
		}
		
		System.out.println("Sender after : " +email.getFromAddress()+" "+tempCount);
		tempCount++;
		//String sender = sendEm.get(sendCheckCounter).getEmail();
		String sender = email.getFromAddress().toString();
		sendCheckCounter++;
		if (sendCheckCounter > sendEm.size() - 1) {
			sendCheckCounter = 0;         //commeted by me on friday
		}
		
		return sender;
	}

}