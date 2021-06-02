package com.josh.emailFunctionality.configuration;

import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.DefaultSingletonBeanRegistry;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.josh.emailFunctionality.EmailFunctionalityApplication;
import com.josh.emailFunctionality.entity.EmailRegistration;
import com.josh.emailFunctionality.service.IEmailRegisterService;

@Configuration
public class ThreadPoolTaskSchedulerConfig {
	@Autowired
	IEmailRegisterService service;

	@Bean(name = "CustomThreadConfig")
	public ScheduledThreadPoolExecutor scheduledThreadPoolExecutor() {
		List<EmailRegistration> emails = service.getAllEmails();
		int size;
		if (emails.size() == 0)
			size = 1;
		else
			size = emails.size();
		int multiplier = 0;
		if (size == 1)
			multiplier = 2;
		else
			multiplier = 1;
		ScheduledThreadPoolExecutor taskExecutor = new ScheduledThreadPoolExecutor(size * multiplier);
		taskExecutor.setMaximumPoolSize(size * multiplier);
		taskExecutor.setCorePoolSize(size * multiplier);
		return taskExecutor;

	}

	public void reintialiseBean(ScheduledThreadPoolExecutor scheduledThreadPoolExec,int size) {

		ConfigurableApplicationContext context = EmailFunctionalityApplication.context;
		DefaultSingletonBeanRegistry registry = (DefaultSingletonBeanRegistry) context.getBeanFactory();
		ScheduledThreadPoolExecutor customeExecutor = (ScheduledThreadPoolExecutor) registry
				.getSingleton("CustomThreadConfig");
		if (customeExecutor.getCorePoolSize() < scheduledThreadPoolExec.getCorePoolSize()) {
			customeExecutor.setMaximumPoolSize(scheduledThreadPoolExec.getMaximumPoolSize());
			customeExecutor.setCorePoolSize(scheduledThreadPoolExec.getCorePoolSize());
		} else {
			customeExecutor.setCorePoolSize(scheduledThreadPoolExec.getCorePoolSize());
			customeExecutor.setMaximumPoolSize(scheduledThreadPoolExec.getMaximumPoolSize());
		}

	}
}
