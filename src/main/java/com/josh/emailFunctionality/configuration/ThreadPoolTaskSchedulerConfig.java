package com.josh.emailFunctionality.configuration;

import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.DefaultSingletonBeanRegistry;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.josh.emailFunctionality.EmailFunctionalityApplication;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import com.josh.emailFunctionality.entity.EmailRegistration;
import com.josh.emailFunctionality.service.IEmailRegisterService;

@Configuration
public class ThreadPoolTaskSchedulerConfig {
	@Autowired
	 IEmailRegisterService service;
	
	 @Bean(name="CustomThreadConfig")
	    public  ScheduledThreadPoolExecutor scheduledThreadPoolExecutor( ) 
	 {
		 List<EmailRegistration> emails = service.getAllEmails();
			 int size;
			 if(emails.size()==0)
				 size = 1;
			 else
				 size = emails.size();
			int multiplier=0;
			if(size==1)
				multiplier=2;
			else
				multiplier = 1;
			 ScheduledThreadPoolExecutor taskExecutor = new ScheduledThreadPoolExecutor(size*multiplier);
			 taskExecutor.setMaximumPoolSize(size*multiplier);
			 taskExecutor.setCorePoolSize(size *multiplier);
		 
		 return taskExecutor;
		 
	    }
	 
	 public  void reintialiseBean(ScheduledThreadPoolExecutor scheduledThreadPoolExec)
	 {
		 
			  ConfigurableApplicationContext context =  EmailFunctionalityApplication.context; 
			  DefaultSingletonBeanRegistry registry =(DefaultSingletonBeanRegistry) context.getBeanFactory();
			  ScheduledThreadPoolExecutor customerExecutor =   (ScheduledThreadPoolExecutor) registry.getSingleton("CustomThreadConfig");
			 System.out.println("core " + customerExecutor.getCorePoolSize() + "  max " + customerExecutor.getMaximumPoolSize());
			 System.out.println("new core " + scheduledThreadPoolExec.getCorePoolSize() + "  new max " + scheduledThreadPoolExec.getMaximumPoolSize());
			 if(customerExecutor.getCorePoolSize() < scheduledThreadPoolExec.getCorePoolSize())
			 {
				 customerExecutor.setMaximumPoolSize(scheduledThreadPoolExec.getMaximumPoolSize());
				 customerExecutor.setCorePoolSize(scheduledThreadPoolExec.getCorePoolSize());
			 }
			 else
			 {
				 customerExecutor.setCorePoolSize(scheduledThreadPoolExec.getCorePoolSize());
				 customerExecutor.setMaximumPoolSize(scheduledThreadPoolExec.getMaximumPoolSize());
			 }
			 
			 
	 }
}
