package io.webApp.springbootstarter.fileStorage;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Class to get the file properties
 * 
 * @author satishkumaranbalagan
 *
 */
@ConfigurationProperties(prefix = "file")
public class FileStorageProperties {
	private String uploadDir;

	/**
	 * Get the file upload directory
	 * 
	 * @return uploadDir in String
	 */
	public String getUploadDir() {
		return uploadDir;
	}

	/**
	 * Set the file upload directory
	 * 
	 * @param uploadDir Directory path in String
	 */
	public void setUploadDir(String uploadDir) {
		this.uploadDir = uploadDir;
	}
}
