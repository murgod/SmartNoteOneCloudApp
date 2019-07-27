package io.webApp.springbootstarter.fileStorage;

import java.nio.file.Path;

import org.springframework.web.multipart.MultipartFile;

/**
 * File storage service interface class
 * 
 * @author satishkumaranbalagan
 *
 */
public interface FileStorageService {

	// Store a file
	public String storeFile(MultipartFile file);

	// Delete a file
	public boolean DeleteFile(String file);

	// Get the saved file path
	public Path getFileStorageLocation();

}
