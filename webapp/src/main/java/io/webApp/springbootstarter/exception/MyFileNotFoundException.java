package io.webApp.springbootstarter.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * File NOT FOUND exception class, inherits RuntimeException class
 * 
 * @author satishkumaranbalagan
 *
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class MyFileNotFoundException extends RuntimeException {

	private final static Logger logger = LoggerFactory.getLogger(MyFileNotFoundException.class);

	/**
	 * Log/print the File not found exception
	 * 
	 * @param message in String
	 */
	public MyFileNotFoundException(String message) {
		super(message);
		logger.error(message);
	}

	/**
	 * Throws and Logs/prints the File not found exception
	 * 
	 * @param message in String
	 * @param cause   Throwable object exception
	 */
	public MyFileNotFoundException(String message, Throwable cause) {
		super(message, cause);
		logger.error(message, cause);
	}
}