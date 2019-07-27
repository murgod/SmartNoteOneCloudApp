package io.webApp.springbootstarter.attachments;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.webApp.springbootstarter.attachments.attachment;
import io.webApp.springbootstarter.attachments.attachmentRepository;

/**
 * Attachment table class with helper functions
 * 
 * @author satishkumaranbalagan
 *
 */
@Service
public class attachmentDao {

	@Autowired
	attachmentRepository attachmentRepo;

	/**
	 * Save a attachment
	 * 
	 * @param nt attachment object
	 * @return the saved entity will never be NULL
	 */
	public attachment Save(attachment nt) {
		return attachmentRepo.save(nt);
	}

	/**
	 * get all attachments
	 * 
	 * @return all attachments in List
	 */
	public List<attachment> findAll() {
		return attachmentRepo.findAll();
	}

	/**
	 * get an attachment based on ID
	 * 
	 * @param attachmentid ID of attachment
	 * @return attachment object with given ID
	 */
	public Optional<attachment> attachmentById(String attachmentid) {
		return attachmentRepo.findById(attachmentid);
	}

	/**
	 * delete an attachment based on Id
	 * 
	 * @param attachmentid ID of attachment
	 */
	public void deleteattachment(String attachmentid) {
		attachmentRepo.deleteById(attachmentid);
	}

	/**
	 * Find the list of attachments based on Note ID
	 * 
	 * @param noteID ID of the note in String
	 * @return attachments in List
	 */
	public List<attachment> findBynoteID(String noteID) {
		return attachmentRepo.findBynoteID(noteID);
	}

	/**
	 * Find a specific attachment under a note
	 * 
	 * @param attachmentID ID of attachment in String
	 * @param noteID       ID of the Note in String
	 * @return attachment object on success else NULL
	 */
	public attachment findattachmentUnderAttachmentList(String attachmentID, String noteID) {
		List<attachment> attachmentList = findBynoteID(noteID);
		for (attachment i : attachmentList) {
			if (i.getAttachmentID().equals(attachmentID))
				return i;
		}
		return null;
	}

	/**
	 * Delete a single attachment under a note
	 * 
	 * @param attachmentID ID of attachment in String
	 * @param noteID       ID of the Note in String
	 * @return true on success else false
	 */
	public boolean DeleteattachmentUnderNoteList(String attachmentID, String noteID) {
		List<attachment> attachmentList = findBynoteID(noteID);
		for (attachment i : attachmentList) {
			if (i.getAttachmentID().equals(attachmentID)) {
				deleteattachment(i.getAttachmentID());
				return true;
			}
		}
		return false;
	}

	/**
	 * Delete all attachments under a note
	 * 
	 * @param noteID ID of the Note in String
	 * @return true on success else false
	 */
	public boolean DeleteattachmentUnderNoteID(String noteID) {
		List<attachment> attachmentList = findBynoteID(noteID);
		for (attachment i : attachmentList) {
			deleteattachment(i.getAttachmentID());
			return true;
		}
		return false;
	}
}
