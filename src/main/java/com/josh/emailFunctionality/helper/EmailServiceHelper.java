package com.josh.emailFunctionality.helper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.HtmlEmail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import com.josh.emailFunctionality.entity.EmailRegistration;
import com.josh.emailFunctionality.service.IEmailRegisterService;
import freemarker.template.Configuration;
import freemarker.template.Template;

@Service
public class EmailServiceHelper {

	@Autowired
	private IEmailRegisterService emailRegisterService;


	@Autowired
	private Configuration configuation;

	public static int counter = 0;

	public static int sendCheckCounter = 0;

	public String sendEmailHelper(String to, String token) throws Exception {
		List<EmailRegistration> sendEm = emailRegisterService.getAllEmails();
		HtmlEmail email = new HtmlEmail();
		email.setHostName("smtp.googlemail.com");
		email.setSmtpPort(587);
		email.setAuthenticator(new DefaultAuthenticator(sendEm.get(sendCheckCounter).getEmail(),
				sendEm.get(sendCheckCounter).getPassword()));
		email.setSSLOnConnect(true);
		Template template = configuation.getTemplate("emailTemplate.ftl");
		Map<String, Object> model = new HashMap<>();
		model.put("token", token);
		String templateText = FreeMarkerTemplateUtils.processTemplateIntoString(template, model);
		email.setFrom(sendEm.get(sendCheckCounter).getEmail());
		email.setSubject("Online Drive Invitation");
		email.setHtmlMsg(templateText);
		email.addTo(to);
		email.send();
		String sender = sendEm.get(sendCheckCounter).getEmail();
		sendCheckCounter++;
		if (sendCheckCounter > sendEm.size() - 1) {
			sendCheckCounter = 0;
		}
		System.out.println("Count : Email Sent : " + counter);
		counter++;
		return sender;
	}

}
