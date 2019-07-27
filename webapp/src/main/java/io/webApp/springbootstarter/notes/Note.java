package io.webApp.springbootstarter.notes;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.webApp.springbootstarter.attachments.attachment;

/**
 * Note POJO class
 * 
 * @author satishkumaranbalagan
 *
 */
@Entity
@Table(name = "note")
@EntityListeners(AuditingEntityListener.class)
public class Note {

	@Id
	@GeneratedValue(generator = MyGenerator.generatorName)
	@GenericGenerator(name = MyGenerator.generatorName, strategy = "uuid")

	@Column(name = "id")
	private String id;
	@Column(name = "title")
	private String title;
	@Column(name = "content")
	private String content;
	@Column(name = "created_on")
	@Temporal(TemporalType.TIMESTAMP)
	private Date created_on;
	@Column(name = "last_updated_on")
	@Temporal(TemporalType.TIMESTAMP)
	@LastModifiedDate
	private Date last_updated_on;
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "emailID")
	@JsonIgnore
	private String emailID;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "note")
	@JsonProperty("attachments")
	private List<attachment> attachments;

	/**
	 * default time stamp of Note
	 */
	@PrePersist
	protected void onCreate() {
		created_on = new Date();
		last_updated_on = new Date();
	}

	/**
	 * updates the time stamp
	 */
	@PreUpdate
	public void getUpdateAt() {
		last_updated_on = new Date();
	}

	/**
	 * get the note ID
	 * 
	 * @return id in String
	 */
	public String getId() {
		return id;
	}

	/**
	 * set the note ID
	 * 
	 * @param id in String
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * Get the title of the note
	 * 
	 * @return title in String
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * Set the title of the note
	 * 
	 * @param title in String
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * Get the content of note
	 * 
	 * @return content in String
	 */
	public String getContent() {
		return content;
	}

	/**
	 * Set the content of note
	 * 
	 * @param content in String
	 */
	public void setContent(String content) {
		this.content = content;
	}

	/**
	 * Get the note created date
	 * 
	 * @return created_on in Date object
	 */
	public Date getCreated_on() {
		return created_on;
	}

	/**
	 * Set the note created date
	 * 
	 * @param created_on in Date object
	 */
	public void setCreated_on(Date created_on) {
		this.created_on = created_on;
	}

	/**
	 * get the note's last modified date
	 * 
	 * @return last_updated_on in Date object
	 */
	public Date getLast_updated_on() {
		return last_updated_on;
	}

	/**
	 * set the note's last modified date
	 * 
	 * @param last_updated_on in Date object
	 */
	public void setLast_updated_on(Date last_updated_on) {
		this.last_updated_on = last_updated_on;
	}

	/**
	 * get the Email ID of the note
	 * 
	 * @return emailID in String
	 */
	public String getEmailID() {
		return emailID;
	}

	/**
	 * set the Email ID of the note
	 * 
	 * @param emailID in String
	 */
	public void setEmailID(String emailID) {
		this.emailID = emailID;
	}

	/**
	 * get all attachments under the note
	 * 
	 * @return attachments in List
	 */
	public List<attachment> getAttachments() {
		return attachments;
	}

	/**
	 * set all attachments under the note
	 * 
	 * @param attachments in List
	 */
	public void setAttachments(List<attachment> attachments) {
		this.attachments = attachments;
	}

//	public String getattachments() {
//		
//		createAttachmentString();
//		return attachments;
//	}
//
//	public void setattachments(String attachments)
//	{
//		createAttachmentString();
//	}
//	
//	public String createAttachmentString() {
//		attachmentDao aDao = new attachmentDao();
//		System.out.println("ID : "+ getId());
//		List<attachment> attachmentList = new ArrayList<attachment>(aDao.findBynoteID(getId()));
//		if (attachmentList != null) {
//
//			StringBuilder sb = new StringBuilder();
//			for (attachment i : attachmentList) {
//				sb.append(i.toString());
//				sb.append("\n");
//			}
//			attachments = sb.toString();
//			return attachments;
//		}
//		attachments = "[\"id\":\"\", \"url\":\"\"]";
//		return attachments;
//	}

}
