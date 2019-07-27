package io.webApp.springbootstarter.fileStorage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.InstanceProfileCredentialsProvider;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;

/**
 * AWS cloud file storage service class, implements FileStorageService interface
 * class
 * 
 * @author satishkumaranbalagan
 *
 */
@Service
@Profile("dev")
public class DevFileStorageService implements FileStorageService {

	private AmazonS3 s3client;

	private Path fileStorageLocation;

	private final static Logger logger = LoggerFactory.getLogger(DevFileStorageService.class);

	@Value("${endpointUrl}")
	private String endpointUrl;
	@Value("${bucketName}")
	private String bucketName;
	/*
	 * @Value("${accessKey}") private String accessKey;
	 * 
	 * @Value("${secretKey}") private String secretKey;
	 */

	/**
	 * initializes the AWS client instance
	 */
	@PostConstruct
	private void initializeAmazon() {
		// AWSCredentials credentials = new BasicAWSCredentials(this.accessKey,
		// this.secretKey);
		// this.s3client = new AmazonS3Client(credentials);

		// this.s3client = AmazonS3ClientBuilder.standard().withCredentials(new
		// ProfileCredentialsProvider()).build();
		this.s3client = AmazonS3ClientBuilder.standard().withCredentials(new InstanceProfileCredentialsProvider(false))
				.build();
	}

	/**
	 * Converts, saves and returns the uploaded file in multipart request to a file
	 * 
	 * @param file uploaded multipart request object
	 * @return File object
	 * @throws IOException
	 */
	private File convertMultiPartToFile(MultipartFile file) throws IOException {
		File convFile = new File(file.getOriginalFilename());
		FileOutputStream fos = new FileOutputStream(convFile);
		fos.write(file.getBytes());
		fos.close();
		return convFile;
	}

	/**
	 * get the original name of the file
	 * 
	 * @param multiPart uploaded file received in multi-part request object
	 * @return file name/NULL in String
	 */
	private String generateFileName(MultipartFile multiPart) {
		return multiPart.getOriginalFilename();
	}

	/**
	 * Uploads the given file into AWS S3 bucket
	 * 
	 * @param fileName name of file in String
	 * @param file     exact file object
	 */
	private void uploadFileTos3bucket(String fileName, File file) {
		try {
			s3client.putObject(new PutObjectRequest(bucketName, fileName, file));
		} catch (Exception e) {
			logger.error(e.toString());
		}
		// new PutObjectRequest(bucketName, fileName,
		// file).withCannedAcl(CannedAccessControlList.PublicRead));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.webApp.springbootstarter.fileStorage.FileStorageService#storeFile(org.
	 * springframework.web.multipart.MultipartFile) Function to Store file on AWS
	 * cloud file system
	 * 
	 * @return fileName in String
	 */
	public String storeFile(MultipartFile multipartFile) {
		String fileUrl = "";
		try {
			File file = convertMultiPartToFile(multipartFile);
			String fileName = generateFileName(multipartFile);
			fileStorageLocation = Paths.get(endpointUrl + bucketName);
			fileUrl = fileStorageLocation + fileName;
			uploadFileTos3bucket(fileName, file);
			file.delete();
		} catch (Exception e) {
			logger.error(e.toString());
		}
		return fileUrl;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.webApp.springbootstarter.fileStorage.FileStorageService#DeleteFile(java.
	 * lang.String) Function to delete a file from AWS cloud file system
	 * 
	 * @param file file path in String
	 * 
	 * @return true on success, else false
	 */
	public boolean DeleteFile(String fileUrl) {
		try {
			String fileName = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
			logger.debug("fileName : " + fileName);
			s3client.deleteObject(new DeleteObjectRequest(bucketName, fileName));
		} catch (AmazonServiceException e) {
			// The call was transmitted successfully, but Amazon S3 couldn't process
			// it, so it returned an error response.
			logger.error(e.toString());
			return false;
		} catch (SdkClientException e) {
			// Amazon S3 couldn't be contacted for a response, or the client
			// couldn't parse the response from Amazon S3.
			logger.error(e.toString());
			return false;
		}
		return true;
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