package com.josh.emailFunctionality.configuration;

import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import com.josh.emailFunctionality.entity.EmailRegistration;
import com.josh.emailFunctionality.service.IEmailRegisterService;

@Configuration
public class ThreadPoolTaskSchedulerConfig {
	@Autowired
	 IEmailRegisterService service;
	 @Bean(name="CustomThreadConfig")
	    public ScheduledThreadPoolExecutor scheduledThreadPoolExecutor() {
		 List<EmailRegistration> emailss = service.getAllEmails();
		 ScheduledThreadPoolExecutor taskExecutor
	          = new ScheduledThreadPoolExecutor(emailss.size()*2);
		 	taskExecutor.setCorePoolSize(emailss.size()*2);
		 	taskExecutor.setMaximumPoolSize(emailss.size()*2);
	        return taskExecutor;
	    }

}
