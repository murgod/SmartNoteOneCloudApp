package io.webApp.springbootstarter.register;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * User register POJO class
 * 
 * @author satishkumaranbalagan
 *
 */
@Entity
public class register {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id")
	private Integer ID;
	@Column(name = "email")
	private String email;
	@Column(name = "password")
	private String password;

	/**
	 * default register constructor
	 */
	public register() {

	}

	/**
	 * parameterized register constructor
	 * 
	 * @param email    in String
	 * @param password in String
	 */
	public register(String email, String password) {
		this.email = email;
		this.password = password;
	}

	/**
	 * get email ID of registered user
	 * 
	 * @return emaid in String
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * set email ID of registered user
	 * 
	 * @param email in String
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * get password of registered user
	 * 
	 * @return password in String
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * set password of registered user
	 * 
	 * @param password in String
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * get ID of registered user
	 * 
	 * @return ID in Integer
	 */
	public Integer getID() {
		return ID;
	}

	/**
	 * set ID of registered user
	 * 
	 * @param iD in Integer
	 */
	public void setID(Integer iD) {
		ID = iD;
	}

}
