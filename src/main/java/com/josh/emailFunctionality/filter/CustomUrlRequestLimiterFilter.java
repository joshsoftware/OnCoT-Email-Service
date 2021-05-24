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

import org.springframework.beans.factory.annotation.Autowired;

import com.josh.emailFunctionality.repository.RegisterEmailRepository;
import com.josh.emailFunctionality.service.EmailSendServiceImpl;

@WebFilter("/api/v1/email")
public class CustomUrlRequestLimiterFilter implements Filter {

	@Autowired
	RegisterEmailRepository emailRegrepo;

	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) servletRequest;
		HttpServletResponse response = (HttpServletResponse) servletResponse;
		if (EmailSendServiceImpl.areSendersAvailable) {
			chain.doFilter(request, response);
		} 
		else {
			response.sendError(422, "Daily Quota Exceeded");
		}
	}

}
