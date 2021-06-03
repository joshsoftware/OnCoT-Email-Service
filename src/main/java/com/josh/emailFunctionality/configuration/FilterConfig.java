package com.josh.emailFunctionality.configuration;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.josh.emailFunctionality.filter.CustomUrlRequestLimiterFilter;

@Configuration
public class FilterConfig {
	 @Bean
	 public FilterRegistrationBean < CustomUrlRequestLimiterFilter > filterRegistrationBean() {
	  FilterRegistrationBean < CustomUrlRequestLimiterFilter > registrationBean = new FilterRegistrationBean<CustomUrlRequestLimiterFilter>();
	  CustomUrlRequestLimiterFilter customURLFilter = new CustomUrlRequestLimiterFilter();
	  registrationBean.setFilter(customURLFilter);
	  registrationBean.addUrlPatterns("/api/v1/email");
	  return registrationBean;
	 }

}
