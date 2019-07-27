package io.webApp.springbootstarter.fileStorage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import io.webApp.springbootstarter.exception.FileStorageException;
import io.webApp.springbootstarter.exception.MyFileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

/**
 * Local device File storage service class, implements FileStorageService
 * interface class
 * 
 * @author satishkumaranbalagan
 *
 */
@Service
@Profile("default")
public class DefaultFileStorageService implements FileStorageService {

	private final Path fileStorageLocation;
	private final static Logger logger = LoggerFactory.getLogger(DefaultFileStorageService.class);

	/**
	 * DefaultFileStorageService constructor
	 * 
	 * @param fileStorageProperties FileStorageProperties object
	 */
	@Autowired
	public DefaultFileStorageService(FileStorageProperties fileStorageProperties) {
		this.fileStorageLocation = Paths.get(fileStorageProperties.getUploadDir()).toAbsolutePath().normalize();

		try {
			Files.createDirectories(this.fileStorageLocation);
		} catch (Exception ex) {
			throw new FileStorageException("Could not create the directory where the uploaded files will be stored.",
					ex);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.webApp.springbootstarter.fileStorage.FileStorageService#storeFile(org.
	 * springframework.web.multipart.MultipartFile) Function to Store file on
	 * system's local disk
	 * 
	 * @return fileName in String
	 */
	public String storeFile(MultipartFile file) {
		// Normalize file name
		String fileName = StringUtils.cleanPath(file.getOriginalFilename());

		try {
			// Check if the file's name contains invalid characters
			if (fileName.contains("..")) {
				throw new FileStorageException("Sorry! Filename contains invalid path sequence " + fileName);
			}

			// Copy file to the target location (Replacing existing file with the same name)
			Path targetLocation = this.fileStorageLocation.resolve(fileName);
			Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

			return fileName;
		} catch (IOException ex) {
			throw new FileStorageException("Could not store file " + fileName + ". Please try again!", ex);
		}
	}

	/**
	 * Load the File from local file system
	 * 
	 * @param fileName in String
	 * @return Resource object interface for a resource descriptor that abstracts
	 *         from the actual type of underlying resource, such as a file or class
	 *         path resource.
	 */
	public Resource loadFileAsResource(String fileName) {
		try {
			Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
			Resource resource = new UrlResource(filePath.toUri());
			if (resource.exists()) {
				return resource;
			} else {
				throw new MyFileNotFoundException("File not found " + fileName);
			}
		} catch (MalformedURLException ex) {
			throw new MyFileNotFoundException("File not found " + fileName, ex);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.webApp.springbootstarter.fileStorage.FileStorageService#DeleteFile(java.
	 * lang.String) Function to delete a file from local file system
	 * 
	 * @param file file path in String
	 * 
	 * @return true on success, else false
	 */
	public boolean DeleteFile(String file) {

		Path targetLocation = Paths.get(file);
		try {
			return Files.deleteIfExists(targetLocation);
		} catch (Exception ex) {
			logger.error(ex.toString());
			return false;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.webApp.springbootstarter.fileStorage.FileStorageService#
	 * getFileStorageLocation() Function to get the stored file location/path
	 * 
	 * @return Path object containing file path
	 */
	public Path getFileStorageLocation() {
		return fileStorageLocation;
	}

}
