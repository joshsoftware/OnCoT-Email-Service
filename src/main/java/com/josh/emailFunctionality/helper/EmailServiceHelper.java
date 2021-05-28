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

import com.josh.emailFunctionality.entity.EmailEntity;
import com.josh.emailFunctionality.entity.EmailRegistration;
import com.josh.emailFunctionality.repository.RegisterEmailRepository;

import freemarker.template.Configuration;
import freemarker.template.Template;

@Service
public class EmailServiceHelper {

	@Autowired
	private RegisterEmailRepository tempRepo;

	@Autowired
	private Configuration configuation;

	@Autowired
	EncryptionDecryptionHelper helper;
	
	@Autowired
	EmailTemplateHelper emailTemplateHelper;

	@Value("${app.email.url}")
	private String testUrl;
	
	@Value("${app.email.googleForm.url}")
	private String googleFormUrl;

	public static String failedEmailSender = "";
	
	public Map<String, Object> model = new HashMap<>();

	public static int sendCheckCounter = -1;

	public String sendEmailHelper(String to, String token) throws Exception {
		List<EmailRegistration> sendEm = tempRepo.findAllIsAvailable();
		sendCheckCounter++;
		if (sendCheckCounter > sendEm.size() - 1) {
			sendCheckCounter = 0;
		}
		HtmlEmail email = new HtmlEmail();
		email.setHostName("smtp.googlemail.com");
		email.setSmtpPort(587);
		email.setAuthenticator(new DefaultAuthenticator(sendEm.get(sendCheckCounter).getEmail(),
				helper.decrypt(sendEm.get(sendCheckCounter).getPassword())));
		email.setSSLOnConnect(true);
		Template template = configuation.getTemplate("emailTemplate.ftl");
		model.put("token", token);
		model.put("url", testUrl);
		model.put("googleFormUrl", googleFormUrl);
		String templateText = FreeMarkerTemplateUtils.processTemplateIntoString(template, model);
		email.setFrom(sendEm.get(sendCheckCounter).getEmail());
		email.setSubject(emailTemplateHelper.subject);
		email.setHtmlMsg(templateText);
		email.addTo(to);
		try {
			email.send();
		} catch (Exception e) {
			if (e.getCause().toString().contains("com.sun.mail.smtp.SMTPSendFailedException")) {
				failedEmailSender = email.getFromAddress().toString();
			}
			throw e;
		}
		String sender = email.getFromAddress().toString();
		return sender;
	}
	
	public String resendEmail(EmailEntity emailCustom) throws Exception
	{
		String sender = sendEmailHelper(emailCustom.getEmail(),
				emailCustom.getToken());
		emailCustom.setSender(sender);
		return sender;
	}
}
