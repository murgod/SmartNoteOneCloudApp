package io.webApp.springbootstarter.attachments;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.webApp.springbootstarter.notes.Note;

/**
 * Attachment POJO class
 * 
 * @author satishkumaranbalagan
 *
 */
@Entity
@Table(name = "attachment")
@EntityListeners(AuditingEntityListener.class)
public class attachment {

	@Id
	@Column(name = "attachmentId")
	@GeneratedValue(generator = "UUID")
	@GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
	private String attachmentID;

	@Column(name = "url")
	private String url;

	@Column(name = "noteID")
	@JsonIgnore
	private String noteID;

	@Column(name = "MetaData", length = 2048)
	@JsonIgnore
	private String mD;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "NOTE_ID", nullable = false)
	@OnDelete(action = OnDeleteAction.CASCADE)
	@JsonIgnore
	private Note note;

	/**
	 * Get Attachment ID
	 * 
	 * @return attachmentID in String
	 */
	public String getAttachmentID() {
		return attachmentID;
	}

	/**
	 * Set Attachment ID
	 * 
	 * @param attachmentID in String
	 */
	public void setAttachmentID(String attachmentID) {
		this.attachmentID = attachmentID;
	}

	/**
	 * Get Metadata of attachment
	 * 
	 * @return mD in String
	 */
	public String getmD() {
		return mD;
	}

	/**
	 * Set Metadata of attachment
	 * 
	 * @param mD in String
	 */
	public void setmD(metaData mD) {
		this.mD = mD.toString();
	}

	/**
	 * Get the URL of the attachment
	 * 
	 * @return url in String
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * Set the URL of the attachment
	 * 
	 * @param url in String
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * Get the ID of the Note
	 * 
	 * @return noteID in String
	 */
	public String getNoteID() {
		return noteID;
	}

	/**
	 * Set the ID of the Note
	 * 
	 * @param noteID in String
	 */
	public void setNoteID(String noteID) {
		this.noteID = noteID;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString() Attachment object in String
	 */
	@Override
	public String toString() {
		return "[\"id\":\"" + attachmentID + ", \"url\":\"" + url + "]";
	}

	/**
	 * Get the Note of attachment
	 * 
	 * @return note Object
	 */
	public Note getNote() {
		return note;
	}

	/**
	 * Set the Note of attachment
	 * 
	 * @param note Object
	 */
	public void setNote(Note note) {
		this.note = note;
	}

}
