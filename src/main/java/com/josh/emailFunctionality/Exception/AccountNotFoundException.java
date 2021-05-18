package com.josh.emailFunctionality.Exception;

@SuppressWarnings("serial")
public class AccountNotFoundException extends RuntimeException{
	
	public AccountNotFoundException(String message)
	{
		super(message);
	}
	public AccountNotFoundException()
	{
		super();
	}
	
}
