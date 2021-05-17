package com.josh.emailFunctionality.Exception;

public class NoEmailAccountsRegisteredException extends RuntimeException{
	
	public NoEmailAccountsRegisteredException(String message)
	{
		super(message);
	}
	public NoEmailAccountsRegisteredException()
	{
		super();
	}
	
}
