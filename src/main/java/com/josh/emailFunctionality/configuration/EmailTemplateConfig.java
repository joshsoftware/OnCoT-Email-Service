package com.josh.emailFunctionality.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.ui.freemarker.FreeMarkerConfigurationFactoryBean;

@Configuration
public class EmailTemplateConfig {
	

	
	@Primary
	@Bean
	public FreeMarkerConfigurationFactoryBean emailTemplateFactoryBean()
	{
		FreeMarkerConfigurationFactoryBean bean = new FreeMarkerConfigurationFactoryBean();
//		StringTemplateLoader stringLoader = new StringTemplateLoader();
//		EmailTemplateEntity firstTemplate = repo.findById(1L).get();
//		String body = firstTemplate.getBody();
//		stringLoader.putTemplate(body, body);
		bean.setTemplateLoaderPath("classpath:/templates");
		bean.setDefaultEncoding("UTF-8");
		return bean;
	}

}
