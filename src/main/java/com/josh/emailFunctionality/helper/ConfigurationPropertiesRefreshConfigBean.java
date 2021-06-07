package com.josh.emailFunctionality.helper;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Component
@Setter
@Getter
public class ConfigurationPropertiesRefreshConfigBean {

	@Value("${app.email.url}")
	private String appemailurl;
	
}
