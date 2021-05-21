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

import com.josh.emailFunctionality.helper.EmailServiceHelper;
import com.josh.emailFunctionality.repository.RegisterEmailRepository;
import com.josh.emailFunctionality.service.EmailSendServiceImpl;

@WebFilter("/api/v1/email")
public class CustomUrlRequestLimiterFilter implements Filter {

	@Autowired
	RegisterEmailRepository emailRegrepo;

	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain)
			throws IOException, ServletException {
		System.out.println("Intercepte by filters");
		System.out.println("Fag : "+EmailSendServiceImpl.areSendersAvailable);
		HttpServletRequest request = (HttpServletRequest) servletRequest;
		HttpServletResponse response = (HttpServletResponse) servletResponse;
		//System.out.println("Size : "+emailRegrepo.findAllIsAvailable());
		if (EmailSendServiceImpl.areSendersAvailable) {  //!emailRegrepo.findAllIsAvailable().isEmpty()		
			chain.doFilter(request, response);
		} else {
			response.sendError(500, "Daily Quota Exceeded");
		}
	}

}
