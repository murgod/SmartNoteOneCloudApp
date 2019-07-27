package io.webApp.springbootstarter.register;

import org.springframework.stereotype.Service;

/**
 * user register service class
 * 
 * @author satishkumaranbalagan
 *
 */
@Service
public class registerService {

	TableCreation tableCreation;

	/**
	 * fucntion to create database
	 */
	public void dbCreation() {
		tableCreation.createDB();
	}
}
