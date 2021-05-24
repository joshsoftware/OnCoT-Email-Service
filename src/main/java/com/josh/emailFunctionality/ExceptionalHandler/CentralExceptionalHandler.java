package com.josh.emailFunctionality.ExceptionalHandler;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import com.josh.emailFunctionality.Exception.AccountNotFoundException;
import com.josh.emailFunctionality.Exception.NoEmailAccountsRegisteredException;
import com.josh.emailFunctionality.common.Response;

@ControllerAdvice
public class CentralExceptionalHandler {
	private DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

	@ExceptionHandler(AccountNotFoundException.class)
	public ResponseEntity<?> handleAccountNotFoundException(Exception ex, WebRequest rq) {
		Response err = new Response();
		err.setStatus("Error");
		err.setError(ex.getMessage());
		err.setTimeStamp(LocalDateTime.now().format(format));
		return new ResponseEntity<Object>(err, HttpStatus.UNAUTHORIZED);
	}

	@ExceptionHandler(org.postgresql.util.PSQLException.class)
	public ResponseEntity<?> handleBadCredentialsException(Exception ex, WebRequest rq) {
		Response err = new Response();
		err.setStatus("Error");
		err.setError("Email account already exist");
		return new ResponseEntity<Object>(err, HttpStatus.UNAUTHORIZED);
	}

	@ExceptionHandler(EmptyResultDataAccessException.class)
	public ResponseEntity<?> handleEmailNotFoundException(Exception ex, WebRequest rq) {
		Response err = new Response();
		err.setStatus("Error");
		err.setError("Email account not found with given id");
		return new ResponseEntity<Object>(err, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(NoEmailAccountsRegisteredException.class)
	public ResponseEntity<?> handleNoEmailRegistedException(Exception ex, WebRequest rq) {
		Response err = new Response();
		err.setStatus("Error");
		err.setError(ex.getMessage());
		return new ResponseEntity<Object>(err, HttpStatus.INTERNAL_SERVER_ERROR);
	}
}
