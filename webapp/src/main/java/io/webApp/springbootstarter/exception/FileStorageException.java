package io.webApp.springbootstarter.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * File storage exception class to throw and print errors, inherits
 * RuntimeException
 * 
 * @author satishkumaranbalagan
 *
 */
public class FileStorageException extends RuntimeException {
	private final static Logger logger = LoggerFactory.getLogger(FileStorageException.class);

	/**
	 * Log/print the File storage exception
	 * 
	 * @param message in String
	 */
	public FileStorageException(String message) {
		super(message);
		logger.error(message);
	}

	/**
	 * Throws and Logs/prints the File storage exception
	 * 
	 * @param message in String
	 * @param cause   Throwable object exception
	 */
	public FileStorageException(String message, Throwable cause) {
		super(message, cause);
		logger.error(message, cause);
	}
}
