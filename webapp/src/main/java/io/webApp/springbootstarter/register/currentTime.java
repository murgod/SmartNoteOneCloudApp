package io.webApp.springbootstarter.register;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Current time generator class
 * 
 * @author satishkumaranbalagan
 *
 */
public class currentTime {

	private DateFormat dateFormat; // importing the simple data format
	private Date date; // using Class of Date and creating an instance

	/**
	 * currentTime constructor
	 */
	public currentTime() {
		super();
		this.dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		this.date = new Date(); // store the value in the date instance variable
	}

	/**
	 * get the current time
	 * 
	 * @return date and formatted time in String
	 */
	public String getCurrentTime() {
		return dateFormat.format(date); // 2016/11/16 12:08:43
	}
}
