package io.webApp.springbootstarter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import io.webApp.springbootstarter.fileStorage.FileStorageProperties;

@SpringBootApplication
@EnableConfigurationProperties({ FileStorageProperties.class })
public class WebApiApp {
	private final static Logger logger = LoggerFactory.getLogger(WebApiApp.class);

	/**
	 * Web App Main method and starting point 
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		logger.info("\n###############Application running##################\n");
		SpringApplication.run(WebApiApp.class, args);
	}

}
