package com.josh.emailFunctionality.Exception;

@SuppressWarnings("serial")
public class NoEmailAccountsRegisteredException extends RuntimeException {

	public NoEmailAccountsRegisteredException(String message) {
		super(message);
	}

	public NoEmailAccountsRegisteredException() {
		super();
	}

}
