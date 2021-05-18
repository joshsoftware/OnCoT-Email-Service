package com.josh.emailFunctionality.helper;

import java.util.concurrent.ScheduledThreadPoolExecutor;

import org.springframework.stereotype.Service;

@Service
public class EmailRegisterHelper {

	public ScheduledThreadPoolExecutor reinitiateThreadPool(int size)
	{
		int multiplyer = 0;
		if(size == 1)
			multiplyer=2; 
		else
			multiplyer=1;
		
		ScheduledThreadPoolExecutor newScheduledThreadPoolExec = new ScheduledThreadPoolExecutor(size * multiplyer);
		newScheduledThreadPoolExec.setCorePoolSize(size * multiplyer);
		newScheduledThreadPoolExec.setMaximumPoolSize(size * multiplyer);
		return newScheduledThreadPoolExec;
	}
}
