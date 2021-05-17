package com.josh.emailFunctionality.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.josh.emailFunctionality.controller.EmailController;
import com.josh.emailFunctionality.service.EmailSendServiceImpl;

@WebFilter("/api/v1/email")
public class CustomUrlRequestLimiterFilter implements Filter {

	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
		if(!EmailSendServiceImpl.dailyLimitExceeded) {
		System.out.println("Intercepted By filter");
		chain.doFilter(request, response);
		}
		else
		{
			response.sendError(500, "Daily Quota Exceeded");
		}
	}

	
}
