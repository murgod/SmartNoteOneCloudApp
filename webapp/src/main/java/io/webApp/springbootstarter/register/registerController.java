package io.webApp.springbootstarter.register;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.tomcat.util.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.auth.InstanceProfileCredentialsProvider;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;
import com.timgroup.statsd.StatsDClient;

import io.webApp.springbootstarter.attachments.attachment;
import io.webApp.springbootstarter.attachments.attachmentDao;
import io.webApp.springbootstarter.attachments.metaData;
import io.webApp.springbootstarter.fileStorage.FileStorageService;
import io.webApp.springbootstarter.notes.Note;
import io.webApp.springbootstarter.notes.NoteDao;

@RestController
public class registerController {

	private final static Pattern VALID_EMAIL_ADDRESS_REGEX = Pattern
			.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

	private String email;
	private String password;
	public register userDetails;
	private List<attachment> attachmentlist;
	private final static Logger logger = LoggerFactory.getLogger(registerController.class);
	private final String testHTTPGET = "endpoint.test.HTTP.GET";
	private final String userHTTPGET = "endpoint.HTTP.GET";
	private final String userHTTPPOST = "endpoint.user.register.HTTP.POST";
	private final String noteHTTPPOST = "endpoint.note.HTTP.POST";
	private final String noteHTTPGET = "endpoint.note.HTTP.GET";
	private final String noteidHTTPGET = "endpoint.note.id.HTTP.GET";
	private final String noteidHTTPPUT = "endpoint.note.id.HTTP.PUT";
	private final String noteidHTTPDELETE = "endpoint.note.id.HTTP.DELETE";
	private final String noteidattachmentsHTTPPOST = "endpoint.note.id.attachments.HTTP.POST";
	private final String noteidattachmentsHTTPGET = "endpoint.note.id.attachments.HTTP.GET";
	private final String noteidattachmentsidHTTPPUT = "endpoint.note.id.attachments.id.HTTP.PUT";
	private final String noteidattachmentsidHTTPDELETE = "endpoint.note.id.attachments.id.HTTP.DELETE";

	@Autowired
	private UserRepository userRepository;

	@Autowired
	NoteDao noteDao;

	@Autowired
	private FileStorageService fileStorageService;

	@Autowired
	private attachmentDao attachDao;

	@Autowired
	private StatsDClient statsd;

	@Value("${ARN}")
	private String topicArn;

	/* Method to verify the Junit test suite */
	@RequestMapping(method = RequestMethod.GET, value = "/test", produces = "application/json")
	public register fetchuser() {
		statsd.incrementCounter(testHTTPGET);
		register user = new register();
		user.setEmail("qwerty@gmail.com");
		user.setPassword("Admin@123");
		return user;
	}

	@RequestMapping(method = RequestMethod.GET, value = "/health", produces = "application/json")
	public ResponseEntity<String> healthCheck() {
		String reply = "{\"RESPONSE\" : \"Health check successsfull\"}";
		return ResponseEntity.status(HttpStatus.OK).body(reply);
	}

	@RequestMapping(method = RequestMethod.POST, value = "/user/register", produces = "application/json")
	public ResponseEntity<String> addUser(@RequestBody register userDetails) {
		statsd.incrementCounter(userHTTPPOST);
		logger.info("POST request : \"/user/register\"");
		String reply;
		if (userDetails.getEmail() == null || userDetails.getPassword() == null || userDetails.getEmail().isEmpty()
				|| userDetails.getPassword().isEmpty()) {
			logger.error("Credentials should not be empty");
			reply = "{\"RESPONSE\" : \"Credentials should not be empty\"}";
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(reply);
		}

		if (checkVaildEmailAddr(userDetails.getEmail())) {
			if (checkAlreadyPresent(userDetails)) {
				logger.debug("User email already exists. Please Login");
				reply = "{\"RESPONSE\" : \"User email already exists. Please Login\"}";
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(reply);
			}
			if (!isValidPassword(userDetails.getPassword())) {
				logger.error("/nPassword should follow NIST standards/n");
				reply = "{\"RESPONSE\" : \"password should follow NIST standards\"}";
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(reply);
			}
			registerUser(userDetails);
			reply = "{\"RESPONSE\" : \"Registration Successful\"}";
			return ResponseEntity.status(HttpStatus.OK).body(reply);
		} else {
			logger.error("Invalid emailID. Check it out !!!");
			reply = "{\"RESPONSE\" : \"Invalid emailID. Check it out !!!\"}";
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(reply);
		}
	}

	@RequestMapping(method = RequestMethod.GET, value = "/", produces = "application/json")
	public ResponseEntity<String> ValidUser(
			@RequestHeader(value = "Authorization", defaultValue = "noAuth") String auth) {
		statsd.incrementCounter(userHTTPGET);
		logger.info("GET request : \"/\"");
		String status = checkAuth(auth);
		if (status.equals("Success")) {
			String time = "{\"RESPONSE\" : \"" + currentTime() + "\"}";
			return ResponseEntity.status(HttpStatus.OK).body(time);
		} else {
			logger.error(status);
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(status);
		}
	}

	@RequestMapping(method = RequestMethod.POST, value = "/note")
	public ResponseEntity<Note> createNote(@RequestBody Note nt,
			@RequestHeader(value = "Authorization", defaultValue = "noAuth") String auth) {
		statsd.incrementCounter(noteHTTPPOST);
		logger.info("POST request: \"/note\"");
		try {
			String status = checkAuth(auth);
			if (status.equals("Success")) {
				if (nt.getTitle().isEmpty() || nt.getContent().isEmpty()) {
					logger.error("\nTitle/Content empty\n");
					throw new Exception();
				}
				nt.setEmailID(email);

				Note note = noteDao.Save(nt);
				logger.debug("HTTP : 201 created");
				return ResponseEntity.status(HttpStatus.CREATED).body(note);
			} else {
				logger.error("HTTP : 401 unauthorized");
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
			}
		} catch (Exception ex) {
			logger.error("HTTP : 400 bad request");
			logger.error(ex.toString());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}
	}

	@RequestMapping(method = RequestMethod.GET, value = "/note")
	public ResponseEntity<List<Note>> getAllNote(
			@RequestHeader(value = "Authorization", defaultValue = "noAuth") String auth) {
		statsd.incrementCounter(noteHTTPGET);
		logger.info("GET request : \"/note\"");
		String status = checkAuth(auth);
		if (status.equals("Success")) {
			logger.debug("HTTP : 200 created");
			return ResponseEntity.status(HttpStatus.OK).body(noteDao.findByemailID(email));
		} else {
			logger.error("HTTP : 401 unauthorized");
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
	}

	@RequestMapping(method = RequestMethod.GET, value = "/note/{id}")
	public ResponseEntity<Note> getNote(@PathVariable(value = "id") String noteId,
			@RequestHeader(value = "Authorization", defaultValue = "noAuth") String auth) {
		statsd.incrementCounter(noteidHTTPGET);
		logger.info("GET request : \"/note/" + noteId);
		try {
			String status = checkAuth(auth);

			if (status.equals("Success")) {
				Note nt = noteDao.findNoteUnderEmailList(noteId, email);
				if (nt == null) {
					logger.error("Empty note");
					throw new NoSuchElementException();
				}
				logger.debug("HTTP : 200 created");
				return ResponseEntity.status(HttpStatus.OK).body(nt);
			} else {
				logger.error("HTTP : 401 unauthorized");
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
			}
		} catch (NoSuchElementException ex) {
			logger.error("HTTP : 404 not found");
			logger.error(ex.toString());
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
	}

	@RequestMapping(method = RequestMethod.PUT, value = "/note/{id}")
	public ResponseEntity<Note> updateNote(@PathVariable(value = "id") String noteId, @RequestBody Note nt,
			@RequestHeader(value = "Authorization", defaultValue = "noAuth") String auth) {
		statsd.incrementCounter(noteidHTTPPUT);
		logger.info("PUT request : \"/note/" + noteId);
		try {
			String status = checkAuth(auth);
			if (status.equals("Success")) {
				Note originalNote = noteDao.findNoteUnderEmailList(noteId, email);
				if (originalNote == null) {
					logger.error("Empty note");
					throw new NoSuchElementException();
				}
				if (nt.getTitle().isEmpty() && nt.getContent().isEmpty()) {
					logger.error("Title/content empty");
					throw new Exception();
				}

				if (nt.getTitle() != null)
					originalNote.setTitle(nt.getTitle());

				if (nt.getContent() != null)
					originalNote.setContent(nt.getContent());

				originalNote.setEmailID(email);

				Note updateNote = noteDao.Save(originalNote);

				if (updateNote == null) {
					logger.error("Empty note");
					logger.error("HTTP : 400 Bad request");
					return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
				}
				logger.debug("HTTP : 200 OK");
				return ResponseEntity.status(HttpStatus.OK).body(updateNote);
			} else {
				logger.error("HTTP : 401 unauthorized");
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
			}
		} catch (NoSuchElementException ex) {
			logger.error("HTTP : 404 not found");
			logger.error(ex.toString());
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		} catch (Exception ex) {
			logger.error("HTTP : 204 no content");
			logger.error(ex.toString());
			return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
		}
	}

	@RequestMapping(method = RequestMethod.DELETE, value = "/note/{id}")
	public ResponseEntity<Note> deleteNote(@PathVariable(value = "id") String noteId,
			@RequestHeader(value = "Authorization", defaultValue = "noAuth") String auth) {
		statsd.incrementCounter(noteidHTTPDELETE);
		logger.info("DELETE request : \"/note/" + noteId);

		try {
			String status = checkAuth(auth);
			if (status.equals("Success")) {
				if (noteDao.DeleteNoteUnderEmailList(noteId, email)) {
					attachmentlist = attachDao.findBynoteID(noteId);
					for (attachment i : attachmentlist) {
						attachDao.deleteattachment(i.getAttachmentID());

						if (!fileStorageService.DeleteFile(i.getUrl()))
							logger.error("Delete file failed");
						throw new NoSuchElementException();
					}
					logger.debug("HTTP : 200 OK");
					return ResponseEntity.status(HttpStatus.OK).build();
					/*
					 * if (attachDao.DeleteattachmentUnderNoteID(noteId)) { return
					 * ResponseEntity.status(HttpStatus.OK).build(); }else { throw new
					 * NoSuchElementException(); }
					 */
				} else {
					logger.error("Note not present");
					throw new NoSuchElementException();
				}
			} else {
				logger.error("HTTP : 401 unauthorized");
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
			}
		} catch (NoSuchElementException ex) {
			logger.error("HTTP : 400 bad request");
			logger.error(ex.toString());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}
	}

	@RequestMapping(method = RequestMethod.POST, value = "/note/{id}/attachments")
	public ResponseEntity<attachment> attach(@PathVariable(value = "id") String noteId,
			@RequestHeader(value = "Authorization", defaultValue = "noAuth") String auth,
			@RequestParam("file") MultipartFile file) {
		statsd.incrementCounter(noteidattachmentsHTTPPOST);
		logger.info("POST request : \"/note/" + noteId + "/attachments");
		try {
			String status = checkAuth(auth);
			if (status.equals("Success")) {

				attachment aT = new attachment();

				Note nt = noteDao.findNoteUnderEmailList(noteId, email);
				if (nt == null) {
					logger.error("Note empty");
					throw new NoSuchElementException();
				}

				aT.setNote(nt);

				aT.setNoteID(noteId);

				String fileName = fileStorageService.storeFile(file);

				String fileDownloadUri = fileStorageService.getFileStorageLocation() + "/" + file.getOriginalFilename();

				aT.setUrl(fileDownloadUri);

				aT.setmD(new metaData(fileName, fileDownloadUri, file.getContentType(), file.getSize()));

				aT = attachDao.Save(aT);

				logger.debug("HTTP : 200 OK");
				return ResponseEntity.status(HttpStatus.OK).body(aT);
			} else {
				logger.error("HTTP : 401 unauthorized");
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
			}
		} catch (NoSuchElementException ex) {
			logger.error("HTTP : 204 no content");
			logger.error(ex.toString());
			return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
		} catch (Exception ex) {
			logger.error("HTTP : 400 bad request");
			logger.error(ex.toString());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}
	}

	@RequestMapping(method = RequestMethod.GET, value = "/note/{id}/attachments")
	public ResponseEntity<List<attachment>> getAllNoteAttachments(
			@RequestHeader(value = "Authorization", defaultValue = "noAuth") String auth,
			@PathVariable(value = "id") String noteId) {
		statsd.incrementCounter(noteidattachmentsHTTPGET);

		logger.info("GET request : \"/note/" + noteId + "/attachments");
		String status = checkAuth(auth);
		if (status.equals("Success")) {
			logger.debug("HTTP : 200 OK");
			return ResponseEntity.status(HttpStatus.OK).body(attachDao.findBynoteID(noteId));
		} else {
			logger.error("HTTP : 401 unauthorized");
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
	}

	@RequestMapping(method = RequestMethod.PUT, value = "/note/{id}/attachments/{idattachments}")
	public ResponseEntity<attachment> updateNoteAttachments(@PathVariable(value = "id") String noteId,
			@PathVariable(value = "idattachments") String attachmentid,
			@RequestHeader(value = "Authorization", defaultValue = "noAuth") String auth,
			@RequestParam("file") MultipartFile file) {
		statsd.incrementCounter(noteidattachmentsidHTTPPUT);

		logger.info("PUT request : \"/note/" + noteId + "/attachments/" + attachmentid);
		try {
			String status = checkAuth(auth);
			if (status.equals("Success")) {
				Note nt = noteDao.findNoteUnderEmailList(noteId, email);
				if (nt == null) {
					logger.error("Note empty");
					throw new NoSuchElementException();
				}

				attachment aT = attachDao.findattachmentUnderAttachmentList(attachmentid, noteId);

				if (aT == null) {
					logger.error("Attachment empty");
					throw new NoSuchElementException();
				}

				if (!fileStorageService.DeleteFile(aT.getUrl())) {
					logger.error("Delete file empty");
					throw new NoSuchElementException();
				}

				String fileName = fileStorageService.storeFile(file);

				String fileDownloadUri = fileStorageService.getFileStorageLocation() + "/" + file.getOriginalFilename();

				// aT.setAttachmentID(attachmentid);

				aT.setNote(nt);

				aT.setUrl(fileDownloadUri);

				aT.setmD(new metaData(fileName, fileDownloadUri, file.getContentType(), file.getSize()));

				attachment updatedaT = attachDao.Save(aT);

				if (updatedaT == null) {
					logger.error("Attachment NULL/n");
					return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
				}
				logger.debug("HTTP : 200 OK");
				return ResponseEntity.status(HttpStatus.OK).body(updatedaT);
			} else {
				logger.error("HTTP : 401 unauthorized");
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
			}
		} catch (NoSuchElementException ex) {
			logger.error("HTTP : 204 no content");
			logger.error(ex.toString());
			return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
		}
	}

	@RequestMapping(method = RequestMethod.DELETE, value = "/note/{id}/attachments/{idattachments}")
	public ResponseEntity<Note> deleteNoteAttachment(@PathVariable(value = "id") String noteId,
			@PathVariable(value = "idattachments") String attachmentid,
			@RequestHeader(value = "Authorization", defaultValue = "noAuth") String auth) {
		statsd.incrementCounter(noteidattachmentsidHTTPDELETE);

		logger.info("DELETE request : \"/note/" + noteId + "/attachments/" + attachmentid);
		try {
			String status = checkAuth(auth);
			if (status.equals("Success")) {

				Note nt = noteDao.findNoteUnderEmailList(noteId, email);
				if (nt == null) {
					logger.error("Note empty");
					throw new NoSuchElementException();
				}

				attachment aT = attachDao.attachmentById(attachmentid).get();
				if (aT == null) {
					logger.error("Attachment NULL");
					throw new NoSuchElementException();
				}

				if (!fileStorageService.DeleteFile(aT.getUrl())) {
					logger.error("Delete file empty");
					throw new NoSuchElementException();
				}
				if (attachDao.DeleteattachmentUnderNoteList(attachmentid, noteId)) {
					logger.debug("HTTP : 200 OK");
					return ResponseEntity.status(HttpStatus.OK).build();
				} else {
					logger.error("Delete attachment empty");
					throw new NoSuchElementException();
				}
			} else {
				logger.error("HTTP : 401 unauthorized");
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
			}
		} catch (NoSuchElementException ex) {
			logger.error("HTTP : 204 no content");
			logger.error(ex.toString());
			return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
		}
	}

	@RequestMapping(method = RequestMethod.POST, value = "/resetdemo", produces = "application/json")
	public ResponseEntity<String> reserPassword(@RequestBody register userDetails) {
		statsd.incrementCounter(userHTTPPOST);

		logger.info("POST request : \"/reset\"");
		String reply;

		if (userDetails.getEmail() == null) {
			logger.error("Credentials should not be empty");
			reply = "{\"RESPONSE\" : \"User email not provided\"}";
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(reply);
		}

		logger.debug("Reset password for Email id : " + userDetails.getEmail());

		if (checkVaildEmailAddr(userDetails.getEmail())) {
			if (!checkAlreadyPresent(userDetails)) {
				logger.debug("This user is not registered with us");
				reply = "{\"RESPONSE\" : \"User not registered ! Please register\"}";
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(reply);
			}
		}

		AmazonSNS snsClient = AmazonSNSClient.builder().withRegion("us-east-1")
				.withCredentials(new InstanceProfileCredentialsProvider(false)).build();

		String resetEmail = userDetails.getEmail();
		logger.info("Reset Email: " + resetEmail);

		PublishRequest publishRequest = new PublishRequest(topicArn, userDetails.getEmail());
		PublishResult publishResult = snsClient.publish(publishRequest);
		PublishResult publishResult1 = snsClient.publish(publishRequest);
		logger.info("SNS Publish Result: " + publishResult);

		reply = "{\"RESPONSE\" : \"Password Reset Link was sent to your emailID\"}";
		return ResponseEntity.status(HttpStatus.OK).body(reply);
	}

	public String checkAuth(String auth) {
		logger.info("Checking User's Basic Authentication");
		String[] encodedValue = auth.split(" ");
		logger.debug("auth Parameter : " + auth);
		String authValue = encodedValue[1];
		logger.debug("auth Value after split :" + authValue);
		byte[] decoded = Base64.decodeBase64(authValue.getBytes());

		String decodedValue = new String(decoded);
		logger.debug("Decoded Value:" + decodedValue);
		if (decodedValue.contains(":")) {
			String[] credentialValue;
			credentialValue = decodedValue.split(":");
			if (credentialValue.length < 2) {
				logger.error("Credentials should not be empty");
				return "{\"RESPONSE\" : \"Credentials should not be empty\"}";
			}
			email = credentialValue[0];
			password = credentialValue[1];
		} else {
			logger.info("User not registered, Please register");
			return "{\"RESPONSE\" : \"Please register\"}";
		}
		logger.debug("email : " + email + "/t" + "password : " + password);

		// check for empty strings
		if (email.isEmpty() || password.isEmpty()) {
			logger.error("Enter valid credentials");
			return "{\"RESPONSE\" : \"Enter valid Credentials\"}";
		}
		userDetails = new register(email, password);

		if (!checkVaildEmailAddr(email) || !checkAlreadyPresent(userDetails) || !checkPassword(userDetails)) {
			logger.error("Invalid credentials");
			return "{\"RESPONSE\" : \"Invalid credentials\"}";
		}
		return "Success";
	}

	/**
	 * checks the validity of email address and whether it follows NIST standards
	 * 
	 * @param email in String
	 * @return true on success, else false
	 */
	public boolean checkVaildEmailAddr(String email) {
		logger.info("Checking Email ID pattern");
		Matcher mat = VALID_EMAIL_ADDRESS_REGEX.matcher(email);
		return mat.find();
	}

	/**
	 * checks if the user has already registered
	 * 
	 * @param userDetails in register object
	 * @return true on success, else false
	 */
	public boolean checkAlreadyPresent(register userDetails) {
		logger.info("Checking Email ID Already present");
		ArrayList<register> dbList = new ArrayList<>(userRepository.findAll());

		for (register i : dbList) {
			if (i.getEmail().equals(userDetails.getEmail())) {
				logger.debug("already registered");
				return true;
			}
		}
		return false;
	}

	/**
	 * compares the given password is valid and matches with the user registered
	 * password details
	 * 
	 * @param userDetails in register object
	 * @return true on success, else false
	 */
	public boolean checkPassword(register userDetails) {
		logger.info("Checking password");
		ArrayList<register> dbList = new ArrayList<>(userRepository.findAll());

		for (register i : dbList) {
			if (i.getEmail().equals(userDetails.getEmail())) {
				// String password = BCrypt.hashpw(userDetails.getPassword(), BCrypt.gensalt());
				if (BCrypt.checkpw(userDetails.getPassword(), i.getPassword())) {
					logger.debug("It matches");
					return true;
				} else
					logger.error("It does not match");
			}
		}
		return false;
	}

	/**
	 * check if the given password is valid and follows NIST standards
	 * 
	 * @param password in String
	 * @return true on success, else false
	 */
	public boolean isValidPassword(String password) {
		if (!(password.matches("^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$%^&*-]).{8,}$"))) {
			logger.error("Invalid password");
			return false;
		}
		logger.debug("Valid password");
		return true;
	}

	/**
	 * register and save the user details in DB/Table
	 * 
	 * @param userData register object
	 * @return true on success
	 */
	public boolean registerUser(@RequestBody final register userData) {
		String password = BCrypt.hashpw(userData.getPassword(), BCrypt.gensalt());
		logger.debug("Password salt : " + password);
		userData.setPassword(password);
		userRepository.save(userData);
		return true;
	}

	/**
	 * Get the current time
	 * 
	 * @return date and formatted time in String
	 */
	public String currentTime() {
		currentTime Ctime = new currentTime();
		return Ctime.getCurrentTime();
	}

}
