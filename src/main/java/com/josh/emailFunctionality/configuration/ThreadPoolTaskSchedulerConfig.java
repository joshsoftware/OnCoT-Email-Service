package com.josh.emailFunctionality.configuration;

import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;

<<<<<<< HEAD

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.DefaultSingletonBeanRegistry;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.josh.emailFunctionality.EmailFunctionalityApplication;
=======
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

>>>>>>> email-template-complete-functionality
import com.josh.emailFunctionality.entity.EmailRegistration;
import com.josh.emailFunctionality.service.IEmailRegisterService;

@Configuration
public class ThreadPoolTaskSchedulerConfig {
	@Autowired
	 IEmailRegisterService service;
<<<<<<< HEAD
	
	 @Bean(name="CustomThreadConfig")
	    public  ScheduledThreadPoolExecutor scheduledThreadPoolExecutor( ) 
	 {
		 List<EmailRegistration> emails = service.getAllEmails();
			 int size;
			 if(emails.size()==0)
				 size = 1;
			 else
				 size = emails.size();
			
			 ScheduledThreadPoolExecutor taskExecutor = new ScheduledThreadPoolExecutor(size*2);
			 taskExecutor.setMaximumPoolSize(size*2);
			 	taskExecutor.setCorePoolSize(size *2);
		 
		 return taskExecutor;
		 
	    }
	 
	 public  void reintialiseBean(ScheduledThreadPoolExecutor scheduledThreadPoolExec)
	 {
		 
			  ConfigurableApplicationContext context =  EmailFunctionalityApplication.context; 
			  DefaultSingletonBeanRegistry registry =(DefaultSingletonBeanRegistry) context.getBeanFactory();
			  ScheduledThreadPoolExecutor customerExecutor =   (ScheduledThreadPoolExecutor) registry.getSingleton("CustomThreadConfig");
			  customerExecutor.setMaximumPoolSize(scheduledThreadPoolExec.getMaximumPoolSize());
			  customerExecutor.setCorePoolSize(scheduledThreadPoolExec.getCorePoolSize());
			 
	 }
=======
	 @Bean(name="CustomThreadConfig")
	    public ScheduledThreadPoolExecutor scheduledThreadPoolExecutor() {
		 List<EmailRegistration> emailss = service.getAllEmails();
		 ScheduledThreadPoolExecutor taskExecutor
	          = new ScheduledThreadPoolExecutor(emailss.size()*2);
		 	taskExecutor.setCorePoolSize(emailss.size()*2);
		 	taskExecutor.setMaximumPoolSize(emailss.size()*2);
	        return taskExecutor;
	    }

>>>>>>> email-template-complete-functionality
}
